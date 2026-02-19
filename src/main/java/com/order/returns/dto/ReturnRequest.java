package com.order.returns.dto;

import com.order.returns.model.ReturnReason;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Return reason is required")
    private ReturnReason reason;

    @Size(max = 1000, message = "Reason description cannot exceed 1000 characters")
    private String reasonDescription;

    @NotNull(message = "Refund amount is required")
    @Positive(message = "Refund amount must be positive")
    private Double refundAmount;

    @NotNull(message = "Pickup requirement must be specified")
    private Boolean isPickupRequired;

    private String pickupAddress;
}

