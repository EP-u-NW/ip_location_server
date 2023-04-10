package de.ep_u_nw.ip_location_server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import de.ep_u_nw.ip_location_server.database.IPDatabase;
import de.ep_u_nw.ip_location_server.database.IPv4Database;
import de.ep_u_nw.ip_location_server.database.IPv6Database;
import de.ep_u_nw.ip_location_server.grpc.GrpcServer;
import de.ep_u_nw.ip_location_server.http.HttpServer;
import de.ep_u_nw.ip_location_server.util.GitHubFileWatcher;
import de.ep_u_nw.ip_location_server.util.Startable;
import de.ep_u_nw.ip_location_server.util.WaitForEnter;

public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.fromEnvironment();
        List<Startable> tasks = new ArrayList<Startable>();
        tasks.add(new WaitForEnter());
        IPv4Database databaseV4;
        if (config.ipv4Enabled.valueOrDefault()) {
            databaseV4 = new IPv4Database(config.ipv4GithubFileUseGzip.valueOrDefault(),
                    config.ipv4GithubMaxFileSize.valueOrDefault());
            GitHubFileWatcher watcher = new GitHubFileWatcher(config.ipv4GithubUser.valueOrDefault(),
                    config.ipv4GithubRepo.valueOrDefault(), config.ipv4GithubFile.valueOrDefault(),
                    config.ipv4GithubCheckIntervalMS.valueOrDefault(), Main::onFileChange, databaseV4);
            tasks.add(watcher);
            boolean success = databaseV4.updateDatabase(watcher.getLastCommitFileUrl());
            if (!success) {
                throw new RuntimeException(
                        "Unable to initially download or build location database from " + watcher.getLastCommitFileUrl()
                                + "!");
            }
        } else {
            databaseV4 = null;
        }
        IPv6Database databaseV6;
        if (config.ipv6Enabled.valueOrDefault()) {
            databaseV6 = new IPv6Database(config.ipv6GithubFileUseGzip.valueOrDefault(),
                    config.ipv6GithubMaxFileSize.valueOrDefault());
            GitHubFileWatcher watcher = new GitHubFileWatcher(config.ipv6GithubUser.valueOrDefault(),
                    config.ipv6GithubRepo.valueOrDefault(), config.ipv6GithubFile.valueOrDefault(),
                    config.ipv6GithubCheckIntervalMS.valueOrDefault(), Main::onFileChange, databaseV6);
            tasks.add(watcher);
            boolean success = databaseV6.updateDatabase(watcher.getLastCommitFileUrl());
            if (!success) {
                throw new RuntimeException(
                        "Unable to initially download or build location database from " + watcher.getLastCommitFileUrl()
                                + "!");
            }
        } else {
            databaseV6 = null;
        }
        if (config.httpEnabled.valueOrDefault()) {
            HttpServer server = new HttpServer(databaseV4, databaseV6, config.httpHost.valueOrDefault(),
                    config.httpPort.valueOrDefault(), config.httpUseSsl.valueOrDefault(),
                    config.httpSslDir.valueOrDefault(), config.httpDetailedExceptionResponses.valueOrDefault(),
                    config.httpMeUseHeader.valueOrDefault(), config.httpMeHeaderName.valueOrDefault(),
                    config.debug.valueOrDefault());
            tasks.add(server);
        }
        if (config.grpcEnabled.valueOrDefault()) {
            GrpcServer server = new GrpcServer(databaseV4, databaseV6, config.grpcHost.valueOrDefault(),
                    config.grpcPort.valueOrDefault(), config.grpcUseSsl.valueOrDefault(),
                    config.grpcSslDir.valueOrDefault(), config.grpcMeUseHeader.valueOrDefault(),
                    config.grpcMeHeaderName.valueOrDefault());
            tasks.add(server);
        }
        executeTasks(tasks);
    }

    private static void executeTasks(List<Startable> tasks) throws InterruptedException, ExecutionException {
        try {
            CompletableFuture<?>[] futures = new CompletableFuture<?>[tasks.size()];
            for (int i = 0; i < tasks.size(); i++) {
                futures[i] = tasks.get(i).start();
            }
            CompletableFuture.anyOf(futures).get();
        } finally {
            LOGGER.info("Exiting...");
            for (Startable task : tasks) {
                task.stop();
            }
        }
    }

    private static void onFileChange(GitHubFileWatcher watcher, Object database) {
        ((IPDatabase<?>) database).updateDatabase(watcher.getLastCommitFileUrl());
    }
}