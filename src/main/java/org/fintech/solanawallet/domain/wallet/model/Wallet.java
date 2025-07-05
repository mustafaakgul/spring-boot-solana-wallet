package org.fintech.solanawallet.domain.wallet.model;

public class Wallet {
    private String publicKey;
    private String privateKey;
    private double balance;
    private String ownerName;

    public Wallet() {
    }

    public Wallet(String publicKey, String privateKey, double balance, String ownerName) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.balance = balance;
        this.ownerName = ownerName;
    }

    // Getters and Setters
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
