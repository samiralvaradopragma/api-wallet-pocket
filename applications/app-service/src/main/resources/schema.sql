CREATE TABLE IF NOT EXISTS wallets (
                                       id UUID PRIMARY KEY,
                                       user_id VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS pockets (
                                       id UUID PRIMARY KEY,
                                       wallet_id UUID NOT NULL,
                                       name VARCHAR(100) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL, -- 👈 Removimos la coma extra que estaba arriba de este campo
    CONSTRAINT fk_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id UUID PRIMARY KEY,
                                            pocket_id UUID NOT NULL,
                                            description VARCHAR(255) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    CONSTRAINT fk_pocket FOREIGN KEY (pocket_id) REFERENCES pockets(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_pockets_wallet_id ON pockets(wallet_id);
CREATE INDEX IF NOT EXISTS idx_transactions_pocket_id ON transactions(pocket_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_wallet_pocket_name ON pockets(wallet_id, name);