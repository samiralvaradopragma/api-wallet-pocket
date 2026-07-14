package co.com.nequi.walletpocket.wallet.values;

import java.util.UUID;

public record WalletId(UUID value) {
    public WalletId {
        if (value == null) {
            throw new IllegalArgumentException("WalletId cannot be null");
        }
    }

    public static WalletId generate() {
        return new WalletId(UUID.randomUUID());
    }

    public static WalletId fromString(String id) {
        return new WalletId(UUID.fromString(id));
    }
}
