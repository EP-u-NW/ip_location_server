package de.ep_u_nw.ip_location_server.database;

import java.math.BigInteger;
import java.net.Inet6Address;

public class IPv6Database extends IPDatabase<BigInteger> {

    public IPv6Database(boolean useGzip, int maxDownloadFileSize) {
        super(useGzip, maxDownloadFileSize);
    }

    @Override
    protected BigInteger parse(String input) {
        return new BigInteger(input);
    }

    @Override
    protected BigInteger addOne(BigInteger value) {
        return value.add(BigInteger.ONE);
    }

    @Override
    protected BigInteger zero() {
        return BigInteger.ZERO;
    }

    public BigInteger fromInet6Address(Inet6Address address) {
        return new BigInteger(1, address.getAddress());
    }

    public String get(Inet6Address address) {
        return get(fromInet6Address(address));
    }
}
