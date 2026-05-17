package com.wordy.server;

import com.wordy.common.EndpointConfig;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.TimeUnit;

public class WordyServer {

    public static final int DEFAULT_PORT = EndpointConfig.DEFAULT_PORT;

    private final int port;
    private Server server;

    public WordyServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerContext context = ServerContext.createDefault();

        server = ServerBuilder.forPort(port)
                .addService(context.loginController())
                .addService(context.adminController())
                .addService(context.gameController())
                .addService(context.leaderboardController())
                .build()
                .start();

        EndpointConfig.publishBoundPort(port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                WordyServer.this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stop() throws InterruptedException {
        try {
            if (server != null) {
                server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            }
        } finally {
            EndpointConfig.clearPublishedPort();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean explicitPort = args.length > 0 || hasExplicitPortEnv();

        int basePort = args.length > 0 ? Integer.parseInt(args[0]) : EndpointConfig.serverPreferredPort();
        int maxAttempts = explicitPort ? 1 : 10;

        IOException lastFailure = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int tryPort = basePort + attempt;
            WordyServer wordyServer = new WordyServer(tryPort);
            try {
                wordyServer.start();
                if (tryPort != DEFAULT_PORT) {
                    System.out.println("Listening on port " + tryPort + " (default " + DEFAULT_PORT + " was busy).");
                    System.out.println("Clients use this port automatically if started from the same folder.");
                }
                System.out.println("Wordy server started on port " + tryPort);
                wordyServer.blockUntilShutdown();
                return;
            } catch (IOException e) {
                lastFailure = e;
                if (!explicitPort && isPortInUse(e) && attempt + 1 < maxAttempts) {
                    continue;
                }
                printStartFailureHelp(e, tryPort, explicitPort);
                throw e;
            }
        }
        if (lastFailure != null) {
            printStartFailureHelp(lastFailure, basePort + maxAttempts - 1, explicitPort);
            throw lastFailure;
        }
    }

    private static boolean hasExplicitPortEnv() {
        String a = System.getenv("WORDY_SERVER_PORT");
        String b = System.getenv("WORDY_PORT");
        return (a != null && !a.isBlank()) || (b != null && !b.isBlank());
    }

    private static boolean isPortInUse(IOException e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof BindException) {
                return true;
            }
            String msg = t.getMessage();
            if (msg != null && msg.contains("Address already in use")) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    private static void printStartFailureHelp(IOException e, int port, boolean explicitPort) {
        System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
        System.err.println("Ensure words.txt exists in the project folder or on the classpath.");
        if (!isPortInUse(e)) {
            return;
        }
        System.err.println();
        System.err.println("Port appears busy. Either:");
        System.err.println("  • Stop the other process (often another WordyServer). Example:");
        System.err.println("      netstat -ano | findstr :" + port);
        System.err.println("      taskkill /PID <pid_from_last_column> /F");
        System.err.println("  • Or set another port:  $env:WORDY_SERVER_PORT=\"9091\"");
        if (!explicitPort) {
            System.err.println("    (With no env set, the server tries ports " + DEFAULT_PORT + ".." + (DEFAULT_PORT + 9) + " automatically.)");
        }
    }
}
