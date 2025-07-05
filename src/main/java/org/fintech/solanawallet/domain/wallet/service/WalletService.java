package org.fintech.solanawallet.domains.wallet.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import software.sava.client.SavaClient;
import software.sava.wallet.Wallet;

import java.util.HashMap;
import java.util.Map;

@Service
public class WalletService {

    private final SavaClient savaClient;

    public WalletService(@Value("${sava.api.key}") String apiKey,
                         @Value("${solana.network:devnet}") String network) {
        this.savaClient = new SavaClient.Builder()
                .setApiKey(apiKey)
                .setNetwork(network)
                .build();
    }

    public Map<String, String> createWallet() {
        Wallet wallet = savaClient.createWallet();

        Map<String, String> response = new HashMap<>();
        response.put("publicKey", wallet.getPublicKey());
        response.put("walletId", wallet.getWalletId());

        return response;
    }

    public double getBalance(String publicKey) {
        try {
            return savaClient.getBalance(publicKey);
        } catch (Exception e) {
            throw new RuntimeException("Bakiye alınırken hata oluştu: " + e.getMessage(), e);
        }
    }
}
