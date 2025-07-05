package org.fintech.solanawallet.domains.wallet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.sava.client.SavaClient;
import software.sava.transaction.TransactionRequest;
import software.sava.transaction.TransactionResponse;

@Service
public class TransferService {

    private final SavaClient savaClient;

    public TransferService(@Value("${sava.api.key}") String apiKey,
                           @Value("${solana.network:devnet}") String network) {
        this.savaClient = new SavaClient.Builder()
                .setApiKey(apiKey)
                .setNetwork(network)
                .build();
    }

    public String transfer(String walletId, String toPublicKey, double solAmount) {
        try {
            TransactionRequest request = new TransactionRequest.Builder()
                    .setWalletId(walletId)
                    .setRecipient(toPublicKey)
                    .setAmount(solAmount)
                    .build();

            TransactionResponse response = savaClient.transfer(request);
            return response.getSignature();
        } catch (Exception e) {
            throw new RuntimeException("Transfer işlemi başarısız oldu: " + e.getMessage(), e);
        }
    }
}
