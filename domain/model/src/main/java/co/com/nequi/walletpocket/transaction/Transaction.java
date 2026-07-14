package co.com.nequi.walletpocket.transaction;


import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.values.Money;

public record Transaction(TransactionId id, Description description, Money amount) {

    public Transaction updateAmount(Money newAmount) {
        return new Transaction(this.id, this.description, newAmount);
    }
}
