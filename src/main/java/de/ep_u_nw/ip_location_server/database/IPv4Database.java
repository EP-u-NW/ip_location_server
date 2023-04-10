package de.ep_u_nw.ip_location_server.database;

import java.math.BigInteger;
import java.net.Inet4Address;

public class IPv4Database extends IPDatabase<Long> {

    public IPv4Database(boolean useGzip, int maxDownloadFileSize) {
        super(useGzip, maxDownloadFileSize);
    }

    @Override
    protected Long parse(String input) {
        return Long.valueOf(input);
    }

    @Override
    protected Long addOne(Long value) {
        return value + 1;
    }

    @Override
    protected Long zero() {
        return 0L;
    }

    public Long fromInet4Address(Inet4Address address) {
        return new BigInteger(1, address.getAddress()).longValue();
    }

    public String get(Inet4Address address) {
        return get(fromInet4Address(address));
    }

}
