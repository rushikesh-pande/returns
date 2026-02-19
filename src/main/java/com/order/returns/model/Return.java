package com.order.returns.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String returnId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnReason reason;

    @Column(length = 1000)
    private String reasonDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;

    @Column(nullable = false)
    private Double refundAmount;

    @Column
    private String shippingLabelUrl;

    @Column
    private String trackingNumber;

    @Column(nullable = false)
    private LocalDateTime requestedDate;

    @Column
    private LocalDateTime approvedDate;

    @Column
    private LocalDateTime receivedDate;

    @Column
    private LocalDateTime refundedDate;

    @Column
    private String processingNotes;

    @Column(nullable = false)
    private Boolean isPickupRequired;

    @Column
    private String pickupAddress;

    @PrePersist
    protected void onCreate() {
        requestedDate = LocalDateTime.now();
        if (returnId == null) {
            returnId = "RET-" + System.currentTimeMillis();
        }
    }
}

