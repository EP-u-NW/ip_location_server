package de.ep_u_nw.ip_location_server.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import de.ep_u_nw.ip_location_server.http.HttpException;
import de.ep_u_nw.ip_location_server.http.HttpException.GenericHttpException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GitHubFileWatcher extends Startable {
    private final static Logger LOGGER = Logger.getLogger(GitHubFileWatcher.class.getSimpleName());

    public static interface OnFileChange {
        public void onChange(GitHubFileWatcher watcher, Object tag);
    }

    private final OkHttpClient httpClient;
    private final String apiUrl;
    private final String githubUser;
    private final String githubRepo;
    private final String githubFile;
    private final long checkIntervallMs;
    private Timer timer;
    private String lastCommit;
    private CompletableFuture<Void> future;
    private boolean inExecution;
    private final OnFileChange onFileChange;
    private final Object tag;

    public GitHubFileWatcher(String githubUser, String githubRepo, String githubFile, long checkIntervallMs,
            OnFileChange onFileChange, Object tag)
            throws IOException {
        this.onFileChange = onFileChange;
        this.githubUser = githubUser;
        this.githubRepo = githubRepo;
        this.githubFile = githubFile;
        this.checkIntervallMs = checkIntervallMs;
        this.tag = tag;
        this.apiUrl = constructApiUrl(githubUser, githubRepo, githubFile);
        this.httpClient = new OkHttpClient();
        this.lastCommit = queryLastCommit();
    }

    private static String constructApiUrl(String user, String repo, String file) {
        return "https://api.github.com/repos/" + user + "/" + repo + "/commits?per_page=1&path=" + file;
    }

    public String getLastCommitFileUrl() {
        return "https://github.com/" + githubUser + "/" + githubRepo + "/raw/" + getLastCommitWithFile()
                + "/" + githubFile;
    }

    public String getLastCommitWithFile() {
        if (lastCommit == null) {
            throw new IllegalStateException("Can not return sha of commit before querying a commit!");
        }
        return lastCommit;
    }

    private synchronized String queryLastCommit() throws IOException {
        Request request = new Request.Builder().url(apiUrl).build();
        String jsonString;
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                jsonString = response.body().string();
            } else {
                throw new GenericHttpException(response.code(), "Unexpected status while accessing " + apiUrl);
            }
        } catch (HttpException e) {
            throw new IOException(e);
        }
        JSONArray result;
        try {
            result = new JSONArray(jsonString);
        } catch (JSONException e) {
            throw new ApiSchemaException(
                    "Could not parse JSON, this could mean that the API scheme changed. JSON was:\r\n"
                            + jsonString,
                    e);
        }
        if (result.length() == 0) {
            throw new ApiSchemaException(
                    "Expected exactly one entry in resulting JSON array but got zero. This means that there is either no commit or that the API scheme changed. JSON was:\r\n"
                            + jsonString);
        }
        if (result.length() > 1) {
            throw new ApiSchemaException(
                    "Expected exactly one entry in resulting JSON array but there are " + result.length()
                            + ". JSON was:\r\n" + jsonString);
        }
        String commit = result.getJSONObject(0).getString("sha");
        if (commit == null) {
            throw new ApiSchemaException(
                    "Expected a sha property in JSON result but there is none. JSON was:\r\n" + jsonString);
        }
        return commit;
    }

    @Override
    protected CompletableFuture<Void> startChecked() {
        future = new CompletableFuture<Void>();
        timer = new Timer("GitHubFileWatcher " + githubFile);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                inExecution = true;
                try {
                    check(true);
                    inExecution = false;
                    if (timer == null && !future.isDone()) {
                        LOGGER.info("Stopped watching " + githubFile);
                        future.complete(null);
                    }
                } catch (Throwable ex) {
                    inExecution = false;
                    future.completeExceptionally(ex);
                }
            }
        }, checkIntervallMs, checkIntervallMs);
        return future;
    }

    private void check(boolean catchQueryExceptions) throws IOException {
        LOGGER.info("Checking for new version of " + githubFile + "...");
        String currentCommit;
        if (catchQueryExceptions) {
            try {
                currentCommit = queryLastCommit();
            } catch (IOException e) {
                LOGGER.warning(
                        "Could not check GitHub API for new commits. Will continue using old dataset! Exception was:");
                e.printStackTrace();
                return;
            }
        } else {
            currentCommit = queryLastCommit();
        }
        if (!currentCommit.equals(lastCommit)) {
            lastCommit = currentCommit;
            LOGGER.info("Found new version of " + githubFile);
            onFileChange.onChange(this, tag);
        } else {
            LOGGER.info("No new version of " + githubFile);
        }
    }

    @Override
    protected void stopChecked() {
        LOGGER.info("Stop watching " + githubFile + "...");
        timer.cancel();
        timer = null;
        if (!future.isDone() && !inExecution) {
            LOGGER.info("Stopped watching " + githubFile);
            future.complete(null);
        }
    }

    public static class ApiSchemaException extends IOException {
        public ApiSchemaException(String message) {
            super(message);
        }

        public ApiSchemaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
