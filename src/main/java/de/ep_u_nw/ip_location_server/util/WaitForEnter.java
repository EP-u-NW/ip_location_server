package de.ep_u_nw.ip_location_server.util;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class WaitForEnter extends Startable {
    private final static Logger LOGGER = Logger.getLogger(WaitForEnter.class.getSimpleName());

    @Override
    protected CompletableFuture<Void> startChecked() {
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        Thread waitForEnterThread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (System.console() == null) {
                    LOGGER.info("No terminal attached, running indefinitely ...");
                } else {
                    LOGGER.info("Press enter to shut down");
                    Scanner scan = new Scanner(System.in);
                    scan.hasNextLine();
                    scan.close();
                    future.complete(null);
                }

            }

        }, "WaitForEnter");
        waitForEnterThread.setDaemon(true);
        waitForEnterThread.start();
        return future;
    }

    @Override
    protected void stopChecked() {
    }

}
