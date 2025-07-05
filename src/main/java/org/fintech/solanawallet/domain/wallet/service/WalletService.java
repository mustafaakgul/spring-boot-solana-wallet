package org.fintech.solanawallet.domain.wallet.service;

import org.fintech.solanawallet.domain.wallet.dto.TransferRequestDTO;
import org.fintech.solanawallet.domain.wallet.dto.WalletRequestDTO;
import org.fintech.solanawallet.domain.wallet.model.Wallet;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.rpc.types.SignatureInformation;
import org.p2p.solanaj.rpc.types.SignatureStatuses;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.core.PublicKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletService {

    private final RpcClient client;
    private final String network;
    // In-memory wallet storage (for demo purposes - in a real app you would use a database)
    private final Map<String, Wallet> walletStore = new ConcurrentHashMap<>();

    public WalletService(@Value("${solana.network:testnet}") String network) {
        this.network = network;
        // Initialize RPC client based on network
        if ("mainnet".equalsIgnoreCase(network)) {
            this.client = new RpcClient("https://api.mainnet-beta.solana.com");
        } else if ("testnet".equalsIgnoreCase(network)) {
            this.client = new RpcClient("https://api.testnet.solana.com");
        } else {
            // Default to devnet
            this.client = new RpcClient("https://api.devnet.solana.com");
        }
    }

    public Wallet createWallet(WalletRequestDTO requestDTO) {
        Account account = new Account();

        // Create new wallet
        Wallet wallet = new Wallet(
            account.getPublicKeyBase58(),
            account.getPrivateKeyBase58(),
            0.0,
            requestDTO.getOwnerName()
        );

        // Store wallet in memory (would be in a database in a real app)
        walletStore.put(wallet.getPublicKey(), wallet);

        return wallet;
    }

    public Double getBalance(String publicKey) {
        try {
                PublicKey pk = new PublicKey(publicKey);
                long lamports = client.getApi().getBalance(pk);
                return lamports / 1_000_000_000.0;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public Double getWallet(String publicKey) {
        try {
            PublicKey pk = new PublicKey(publicKey);
            long lamports = client.getApi().getBalance(pk);
            return lamports / 1_000_000_000.0;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> airdrop(String publicKey, double amount) {
        Map<String, Object> response = new HashMap<>();
        try {
            if ("mainnet".equalsIgnoreCase(network)) {
                response.put("success", false);
                response.put("error", "Airdrop is not supported on mainnet.");
                return response;
            }

            PublicKey pk = new PublicKey(publicKey);
            long lamports = (long) (amount * 1_000_000_000L);
            String txSignature = client.getApi().requestAirdrop(pk, lamports);
            response.put("success", true);
            response.put("signature", txSignature);
            response.put("message", "Airdrop requested. Check transaction status for confirmation.");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    public List<Map<String, Object>> getTransactionHistory(String publicKey) {
        List<Map<String, Object>> txList = new ArrayList<>();
        try {
            PublicKey pk = new PublicKey(publicKey);
            List<SignatureInformation> signatures = client.getApi().getConfirmedSignaturesForAddress2(pk, 20);
            for (SignatureInformation sig : signatures) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("signature", sig.getSignature());
                tx.put("slot", sig.getSlot());
                tx.put("err", sig.getErr());
                tx.put("blockTime", sig.getBlockTime());
                txList.add(tx);
            }
            return txList;
        } catch (Exception e) {
            throw new RuntimeException("İşlem geçmişi alınamadı: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getTransactionStatus(String signature) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> sigList = Collections.singletonList(signature);
            SignatureStatuses statuses = client.getApi().getSignatureStatuses(sigList, true);
            if (statuses != null && statuses.getValue() != null && !statuses.getValue().isEmpty() && statuses.getValue().get(0) != null) {
                Map<String, Object> status = (Map<String, Object>) statuses.getValue().get(0);
                result.put("confirmationStatus", status.get("confirmationStatus"));
                result.put("slot", status.get("slot"));
                result.put("err", status.get("err"));
            } else {
                result.put("confirmationStatus", "not found");
            }
            return result;
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return result;
        }
    }

    public Map<String, Object> validateAddress(String publicKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            new PublicKey(publicKey);
            result.put("valid", true);
        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getNetworkInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("network", this.network);
        result.put("rpcUrl", this.client.getEndpoint());
        return result;
    }
}
