package co.com.nequi.walletpocket.transaction.values;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId fromString(String id) {
        return new TransactionId(UUID.fromString(id));
    }
}
