package co.com.nequi.walletpocket.domain.pocket.values;

import java.util.UUID;

public record PocketId(UUID value) {
    public PocketId {
        if (value == null) {
            throw new IllegalArgumentException("PocketId cannot be null");
        }
    }

    public static PocketId generate() {
        return new PocketId(UUID.randomUUID());
    }

    public static PocketId fromString(String id) {
        return new PocketId(UUID.fromString(id));
    }
}
