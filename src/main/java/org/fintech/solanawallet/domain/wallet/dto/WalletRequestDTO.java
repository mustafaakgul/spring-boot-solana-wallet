package org.fintech.solanawallet.domain.wallet.dto;

public class WalletRequestDTO {
    private String ownerName;

    public WalletRequestDTO() {
    }

    public WalletRequestDTO(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
