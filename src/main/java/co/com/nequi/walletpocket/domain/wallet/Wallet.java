package co.com.nequi.walletpocket.domain.wallet;


import co.com.nequi.walletpocket.domain.pocket.Pocket;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Wallet(WalletId id, String userId, List<Pocket> pockets) {

    public Wallet {
        pockets = Collections.unmodifiableList(new ArrayList<>(pockets));
    }

    public Wallet addPocket(Pocket pocket) {
        List<Pocket> updatedPockets = new ArrayList<>(this.pockets);
        updatedPockets.add(pocket);
        return new Wallet(this.id, this.userId, updatedPockets);
    }

    public Wallet updatePocket(Pocket updatedPocket) {
        List<Pocket> updatedPockets = this.pockets.stream()
                .map(p -> p.id().equals(updatedPocket.id()) ? updatedPocket : p)
                .toList();
        return new Wallet(this.id, this.userId, updatedPockets);
    }
}
