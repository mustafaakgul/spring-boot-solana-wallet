package org.fintech.solanawallet.domains.wallet.controller;

import com.example.solanawallet.dto.TransferRequestDto;
import com.example.solanawallet.service.TransferService;
import com.example.solanawallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TransferService transferService;

    public WalletController(WalletService walletService, TransferService transferService) {
        this.walletService = walletService;
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createWallet() {
        return ResponseEntity.ok(walletService.createWallet());
    }

    @GetMapping("/{publicKey}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable String publicKey) {
        return ResponseEntity.ok(walletService.getBalance(publicKey));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDto transferRequest) {
        String signature = transferService.transfer(
                transferRequest.getWalletId(),
                transferRequest.getToPublicKey(),
                transferRequest.getAmount()
        );
        return ResponseEntity.ok(signature);
    }
}
