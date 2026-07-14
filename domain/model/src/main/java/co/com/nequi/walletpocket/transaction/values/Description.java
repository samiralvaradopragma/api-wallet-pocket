package co.com.nequi.walletpocket.transaction.values;

public record Description(String value) {
    public Description {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be empty");
        }
    }
}
