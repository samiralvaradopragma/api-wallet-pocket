package co.com.nequi.walletpocket.domain.transaction;


import co.com.nequi.walletpocket.domain.transaction.values.Description;
import co.com.nequi.walletpocket.domain.transaction.values.TransactionId;
import co.com.nequi.walletpocket.domain.wallet.values.Money;

public record Transaction(TransactionId id, Description description, Money amount) {

    public Transaction updateAmount(Money newAmount) {
        return new Transaction(this.id, this.description, newAmount);
    }
}
