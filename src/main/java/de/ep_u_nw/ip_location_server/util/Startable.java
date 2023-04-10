package de.ep_u_nw.ip_location_server.util;

import java.util.concurrent.CompletableFuture;

public abstract class Startable {
    public static enum State {
        NOT_STARTED,
        STARTED,
        STOPPED;
    }

    private State state = State.NOT_STARTED;

    public State getState() {
        return state;
    }

    public synchronized CompletableFuture<Void> start() throws IllegalStateException {
        switch (state) {
            case NOT_STARTED:
                state = State.STARTED;
                return startChecked();
            case STARTED:
                throw new IllegalStateException("Already started!");
            case STOPPED:
                throw new IllegalStateException("Can start only once!");
        }
        throw new RuntimeException("Unreachable!");
    }

    protected abstract CompletableFuture<Void> startChecked();

    public synchronized void stop() {
        switch (state) {
            case NOT_STARTED:
                break;
            case STARTED:
                state = State.STOPPED;
                stopChecked();
                break;
            case STOPPED:
                break;
        }
    }

    protected abstract void stopChecked();
}
