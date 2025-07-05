package org.fintech.solanawallet.domain.wallet.service;

import org.fintech.solanawallet.domain.wallet.dto.TransferRequestDTO;
import org.fintech.solanawallet.domain.wallet.model.Wallet;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.types.SignatureStatuses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.p2p.solanaj.rpc.RpcClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransferService {

    private final RpcClient client;
    private final WalletService walletService;
    private final String network;

    public TransferService(WalletService walletService, @Value("${solana.network:testnet}") String network) {
        this.walletService = walletService;
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

    public String transfer(TransferRequestDTO transferRequest) {
        try {
            // Get wallet from the service
            // Wallet sourceWallet = walletService.getWallet(transferRequest.getWalletId());

            // Check if the wallet has enough balance
            /*if (sourceWallet.getBalance() < transferRequest.getAmount()) {
                throw new RuntimeException("Sufficient balance not found in wallet: " + transferRequest.getWalletId());
            }*/

            byte[] senderSecretKey = org.bitcoinj.core.Base58.decode(transferRequest.getWalletId());
            Account sender = new Account(senderSecretKey);

            PublicKey recipient = new PublicKey(transferRequest.getPublicKey());

            long lamports = (long) (transferRequest.getAmount() * 1_000_000_000.0);

            Transaction transaction = new Transaction();
            transaction.addInstruction(
                    SystemProgram.transfer(
                            sender.getPublicKey(),
                            recipient,
                            lamports
                    )
            );

            String signature = client.getApi().sendTransaction(transaction, sender);
            boolean confirmed = false;
            for (int i = 0; i < 5; i++) {
                SignatureStatuses statuses = client.getApi().getSignatureStatuses(List.of(signature), false);
                if (statuses != null && statuses.getValue() != null && !statuses.getValue().isEmpty()) {
                    var status = statuses.getValue().get(0);
                    if (status != null && status.getConfirmationStatus() != null && status.getConfirmationStatus().equals("confirmed")) {
                        confirmed = true;
                        break;
                    }
                }
                Thread.sleep(1000);
            }
            if (!confirmed) {
                throw new RuntimeException("Transaction not confirmed after multiple attempts: " + signature);
            }

            return signature;
        } catch (Exception e) {
            throw new RuntimeException("Transfer successful: " + e.getMessage(), e);
        }
    }
}
