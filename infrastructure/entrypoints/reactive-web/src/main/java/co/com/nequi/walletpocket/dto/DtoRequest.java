package co.com.nequi.walletpocket.dto;

import java.math.BigDecimal;

public class DtoRequest {

    public record CreateWallet(String userId) {}
    public record AddPocket(String name) {}
    public record RegisterTransaction(String description, BigDecimal amount) {}
    public record ModifyTransaction(BigDecimal amount) {}

}
