package com.order.returns.controller;

import com.order.returns.dto.ReturnRequest;
import com.order.returns.dto.ReturnResponse;
import com.order.returns.model.ReturnStatus;
import com.order.returns.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping("/initiate")
    public ResponseEntity<ReturnResponse> initiateReturn(@Valid @RequestBody ReturnRequest request) {
        ReturnResponse response = returnService.initiateReturn(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{returnId}/approve")
    public ResponseEntity<ReturnResponse> approveReturn(@PathVariable String returnId) {
        ReturnResponse response = returnService.approveReturn(returnId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{returnId}/reject")
    public ResponseEntity<ReturnResponse> rejectReturn(
            @PathVariable String returnId,
            @RequestParam String reason) {
        ReturnResponse response = returnService.rejectReturn(returnId, reason);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{returnId}/received")
    public ResponseEntity<ReturnResponse> markAsReceived(@PathVariable String returnId) {
        ReturnResponse response = returnService.markAsReceived(returnId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{returnId}/refund")
    public ResponseEntity<ReturnResponse> processRefund(@PathVariable String returnId) {
        ReturnResponse response = returnService.processRefund(returnId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnResponse> getReturnStatus(@PathVariable String returnId) {
        ReturnResponse response = returnService.getReturnStatus(returnId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReturnResponse>> getCustomerReturns(@PathVariable String customerId) {
        List<ReturnResponse> returns = returnService.getCustomerReturns(customerId);
        return ResponseEntity.ok(returns);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReturnResponse>> getReturnsByStatus(@PathVariable ReturnStatus status) {
        List<ReturnResponse> returns = returnService.getReturnsByStatus(status);
        return ResponseEntity.ok(returns);
    }
}

