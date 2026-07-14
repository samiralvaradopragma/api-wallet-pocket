package co.com.nequi.walletpocket.domain.pocket;

import co.com.nequi.walletpocket.domain.pocket.values.PocketId;
import co.com.nequi.walletpocket.domain.transaction.Transaction;
import co.com.nequi.walletpocket.domain.wallet.values.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Pocket(PocketId id, String name, Money balance, List<Transaction> transactions) {

    public Pocket {
        transactions = Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    public Pocket addTransaction(Transaction transaction) {
        List<Transaction> updatedTransactions = new ArrayList<>(this.transactions);
        updatedTransactions.add(transaction);

        Money updatedBalance = this.balance.add(transaction.amount());

        return new Pocket(this.id, this.name, updatedBalance, updatedTransactions);
    }

    public Pocket modifyTransaction(Transaction updatedTransaction) {
        List<Transaction> updatedTransactions = this.transactions.stream()
                .map(t -> t.id().equals(updatedTransaction.id()) ? updatedTransaction : t)
                .toList();

        Money reevaluatedBalance = updatedTransactions.stream()
                .map(Transaction::amount)
                .reduce(Money.zero(), Money::add);

        return new Pocket(this.id, this.name, reevaluatedBalance, updatedTransactions);
    }
}
