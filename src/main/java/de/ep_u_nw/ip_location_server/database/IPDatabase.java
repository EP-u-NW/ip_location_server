package de.ep_u_nw.ip_location_server.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class IPDatabase<T> {
    private final static Logger LOGGER = Logger.getLogger(IPDatabase.class.getSimpleName());
    private final static int HTTP_OK = 200;
    private final OkHttpClient httpClient;
    private final boolean useGzip;
    private final int maxDownloadFileSize;
    private NavigableMap<T, String> database;

    public IPDatabase(boolean useGzip, int maxDownloadFileSize) {
        this.useGzip = useGzip;
        this.maxDownloadFileSize = maxDownloadFileSize;
        this.httpClient = new OkHttpClient.Builder().followRedirects(true).build();
    }

    public boolean updateDatabase(String fetchUrl) {
        long stepStartTime = System.currentTimeMillis();
        byte[] data = download(fetchUrl);
        if (data == null) {
            LOGGER.warning("Unable to fetch database file in " + (System.currentTimeMillis() - stepStartTime)
                    + "ms. Will continue using old dataset!");
            return false;
        }
        LOGGER.info("Fetched database file (" + data.length + "bytes) in "
                + (System.currentTimeMillis() - stepStartTime) + "ms");
        LOGGER.info("Start building new database...");
        stepStartTime = System.currentTimeMillis();
        try (InputStream inputStream = useGzip ? new GZIPInputStream(new ByteArrayInputStream(data))
                : new ByteArrayInputStream(data)) {
            NavigableMap<T, String> newDatabase = buildDatabase(inputStream);
            database = newDatabase;
            LOGGER.info("Successfully build new database with " + database.size() + " entries in "
                    + (System.currentTimeMillis() - stepStartTime) + "ms");
            return true;
        } catch (IOException e) {
            LOGGER.warning("Failed building new database in " + (System.currentTimeMillis() - stepStartTime)
                    + "ms. Will continue using old dataset! Exceptions was:");
            e.printStackTrace();
            return false;
        }

    }

    public String get(T ip) {
        return database.floorEntry(ip).getValue();
    }

    private byte[] download(String fetchUrl) {
        Request request = new Request.Builder().url(fetchUrl).build();
        LOGGER.info("Start fetching " + fetchUrl);
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == HTTP_OK) {
                int contentLength;
                String contentLengthString = response.headers().get("Content-Length");
                try {
                    contentLength = Integer.parseInt(contentLengthString);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Could not parse Content-Length: " + contentLengthString);
                    return null;
                }
                if (maxDownloadFileSize >= 0) {
                    if (contentLength > maxDownloadFileSize || contentLength < 0) {
                        LOGGER.warning("Invalid Content-Length: 0 <= " + contentLength + " <= " + maxDownloadFileSize
                                + " does not hold!");
                        return null;
                    }
                }
                byte[] data = response.body().bytes();
                return data;
            } else {
                LOGGER.warning("Unexpected response code " + response.code());
                LOGGER.warning("Body was:\r\n" + response.body().string());
                return null;
            }
        } catch (Throwable e) {
            LOGGER.warning("Failed to fetch target due to " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private final static String COMMA_REGEX = Pattern.quote(",");
    private final static String UNKNOWN = null;

    private NavigableMap<T, String> buildDatabase(InputStream inputStream)
            throws IOException {
        NavigableMap<T, String> ipRegionMap = new TreeMap<T, String>();
        ipRegionMap.put(zero(), UNKNOWN);
        Scanner scan = new Scanner(inputStream, StandardCharsets.UTF_8);
        while (scan.hasNextLine()) {
            String[] parts = scan.nextLine().split(COMMA_REGEX, 3);
            T ip = parse(parts[0]);
            T upperBound = addOne(parse(parts[1]));
            ipRegionMap.put(ip, parts[2]);
            ipRegionMap.putIfAbsent(upperBound, UNKNOWN);
        }
        scan.close();
        return ipRegionMap;
    }

    protected abstract T parse(String input);

    protected abstract T addOne(T value);

    protected abstract T zero();
}
