package com.order.returns.dto;

import com.order.returns.model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {

    private Long id;
    private String returnId;
    private String orderId;
    private String customerId;
    private ReturnStatus status;
    private Double refundAmount;
    private String shippingLabelUrl;
    private String trackingNumber;
    private LocalDateTime requestedDate;
    private LocalDateTime approvedDate;
    private LocalDateTime refundedDate;
    private String message;
}

