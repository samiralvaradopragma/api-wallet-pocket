package co.com.nequi.walletpocket.domain.wallet.values;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal value) {
    public Money {
        Objects.requireNonNull(value, "Money value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money value cannot be negative");
        }
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value()));
    }

    public Money subtract(Money other) {
        return new Money(this.value.subtract(other.value()));
    }

    public boolean isGreaterThan(Money other) {
        return this.value.compareTo(other.value()) > 0;
    }
}
