import com.google.protobuf.Empty;

import de.ep_u_nw.ip_location_server.grpc.generated.IP;
import de.ep_u_nw.ip_location_server.grpc.generated.IPBinary;
import de.ep_u_nw.ip_location_server.grpc.generated.IPLocationServiceGrpc;
import de.ep_u_nw.ip_location_server.grpc.generated.IPv4Binary;
import de.ep_u_nw.ip_location_server.grpc.generated.LookupResult;
import de.ep_u_nw.ip_location_server.grpc.generated.IPLocationServiceGrpc.IPLocationServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCTest {
    public static void main(String[] args) {
        IPLocationServiceBlockingStub client = IPLocationServiceGrpc
                .newBlockingStub(
                        ManagedChannelBuilder.forAddress("location.ep-u-nw.de", 443).useTransportSecurity().build());
        System.out.println(formatLookupResult(testGrpcMe(client)));
        System.out.println(formatLookupResult(testGrpcLookup(client, "81.169.207.220")));
        System.out.println(formatLookupResult(testGrpcLookup(client, new int[] { 81, 169, 207, 220 })));
        ((ManagedChannel) client.getChannel()).shutdown();
    }

    private static LookupResult testGrpcMe(IPLocationServiceBlockingStub client) {
        return client.me(Empty.getDefaultInstance());
    }

    private static LookupResult testGrpcLookup(IPLocationServiceBlockingStub client, String ipString) {
        return client.lookup(IP.newBuilder().setIpString(ipString).build());
    }

    private static LookupResult testGrpcLookup(IPLocationServiceBlockingStub client, int[] ip4Binary) {
        return client.lookup(IP.newBuilder()
                .setIpBinary(IPBinary.newBuilder().setV4(IPv4Binary.newBuilder().setB01(ip4Binary[0])
                        .setB02(ip4Binary[1]).setB03(ip4Binary[2]).setB04(ip4Binary[3])))
                .build());
    }

    private static String formatLookupResult(LookupResult result) {
        return (result.getIsV6() ? "v6" : "v4") + "," + result.getIpString() + ","
                + (result.getInfo().isEmpty() ? null : result.getInfo());
    }
}
