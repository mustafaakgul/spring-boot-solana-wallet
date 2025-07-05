package org.fintech.solanawallet.domain.wallet.controller;

import org.fintech.solanawallet.domain.wallet.dto.TransferRequestDTO;
import org.fintech.solanawallet.domain.wallet.dto.WalletRequestDTO;
import org.fintech.solanawallet.domain.wallet.model.Wallet;
import org.fintech.solanawallet.domain.wallet.service.TransferService;
import org.fintech.solanawallet.domain.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TransferService transferService;

    public WalletController(WalletService walletService, TransferService transferService) {
        this.walletService = walletService;
        this.transferService = transferService;
    }

    @PostMapping("")
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequestDTO requestDTO) {
        return ResponseEntity.ok(walletService.createWallet(requestDTO));
    }

    @GetMapping("/{publicKey}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String publicKey) {
        return ResponseEntity.ok(walletService.getBalance(publicKey));
    }

    @GetMapping("/{publicKey}")
    public ResponseEntity<Double> getWallet(@PathVariable String publicKey) {
        return ResponseEntity.ok(walletService.getWallet(publicKey));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO transferRequest) {
        String result = transferService.transfer(transferRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{publicKey}/airdrop")
    public ResponseEntity<Map<String, Object>> airdrop(@PathVariable String publicKey, @RequestParam double amount) {
        Map<String, Object> result = walletService.airdrop(publicKey, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{publicKey}/transactions")
    public ResponseEntity<List<Map<String, Object>>> getTransactionHistory(@PathVariable String publicKey) {
        return ResponseEntity.ok(walletService.getTransactionHistory(publicKey));
    }

    @GetMapping("/transaction/{signature}/status")
    public ResponseEntity<Map<String, Object>> getTransactionStatus(@PathVariable String signature) {
        return ResponseEntity.ok(walletService.getTransactionStatus(signature));
    }

    @GetMapping("/validate/{publicKey}")
    public ResponseEntity<Map<String, Object>> validateAddress(@PathVariable String publicKey) {
        return ResponseEntity.ok(walletService.validateAddress(publicKey));
    }

    @GetMapping("/network")
    public ResponseEntity<Map<String, Object>> getNetworkInfo() {
        return ResponseEntity.ok(walletService.getNetworkInfo());
    }
}
