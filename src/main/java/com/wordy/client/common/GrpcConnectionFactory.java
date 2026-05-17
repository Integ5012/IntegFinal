package com.wordy.client.common;

import com.wordy.common.ClientConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public final class GrpcConnectionFactory {

    private GrpcConnectionFactory() {
    }

    public static ManagedChannel open(ClientConfig.Settings settings) {
        return ManagedChannelBuilder.forAddress(settings.host(), settings.port())
                .usePlaintext()
                .build();
    }

    public static String friendlyConnectionError(Exception ex) {
        if (ex instanceof StatusRuntimeException statusEx) {
            String code = statusEx.getStatus().getCode().name();
            if ("UNAVAILABLE".equals(code)) {
                int published = com.wordy.common.EndpointConfig.readPublishedPort();
                return "Cannot reach the server at the configured host/port.\n\n"
                        + "1. Start the server: gradlew runServer\n"
                        + "2. Check the port in .wordy-grpc-port (currently "
                        + published + ") matches the Port field\n"
                        + "3. Click \"Use server port\" on the connection panel";
            }
            String detail = statusEx.getStatus().getDescription();
            return code + (detail != null && !detail.isBlank() ? ": " + detail : "");
        }
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }
}
