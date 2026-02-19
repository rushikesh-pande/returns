package com.order.returns.service;

import com.order.returns.dto.ReturnRequest;
import com.order.returns.dto.ReturnResponse;
import com.order.returns.kafka.ReturnEventProducer;
import com.order.returns.model.Return;
import com.order.returns.model.ReturnStatus;
import com.order.returns.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnEventProducer eventProducer;
    private final ShippingLabelService shippingLabelService;
    private final RefundService refundService;

    @Transactional
    public ReturnResponse initiateReturn(ReturnRequest request) {
        log.info("Initiating return for order: {}", request.getOrderId());

        // Validate return eligibility
        validateReturnEligibility(request.getOrderId());

        // Create return entity
        Return returnEntity = new Return();
        returnEntity.setOrderId(request.getOrderId());
        returnEntity.setCustomerId(request.getCustomerId());
        returnEntity.setCustomerEmail(request.getCustomerEmail());
        returnEntity.setReason(request.getReason());
        returnEntity.setReasonDescription(request.getReasonDescription());
        returnEntity.setStatus(ReturnStatus.REQUESTED);
        returnEntity.setRefundAmount(request.getRefundAmount());
        returnEntity.setIsPickupRequired(request.getIsPickupRequired());
        returnEntity.setPickupAddress(request.getPickupAddress());

        Return savedReturn = returnRepository.save(returnEntity);

        // Publish return initiated event
        eventProducer.publishReturnInitiated(savedReturn);

        log.info("Return initiated successfully with ID: {}", savedReturn.getReturnId());

        return mapToResponse(savedReturn, "Return request submitted successfully");
    }

    @Transactional
    public ReturnResponse approveReturn(String returnId) {
        log.info("Approving return: {}", returnId);

        Return returnEntity = getReturnByReturnId(returnId);

        if (returnEntity.getStatus() != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only requested returns can be approved");
        }

        returnEntity.setStatus(ReturnStatus.APPROVED);
        returnEntity.setApprovedDate(LocalDateTime.now());

        // Generate shipping label
        String labelUrl = shippingLabelService.generateShippingLabel(returnEntity);
        returnEntity.setShippingLabelUrl(labelUrl);
        returnEntity.setStatus(ReturnStatus.SHIPPING_LABEL_GENERATED);

        Return savedReturn = returnRepository.save(returnEntity);

        // Publish return approved event
        eventProducer.publishReturnApproved(savedReturn);

        log.info("Return approved and shipping label generated: {}", returnId);

        return mapToResponse(savedReturn, "Return approved and shipping label generated");
    }

    @Transactional
    public ReturnResponse rejectReturn(String returnId, String reason) {
        log.info("Rejecting return: {}", returnId);

        Return returnEntity = getReturnByReturnId(returnId);

        if (returnEntity.getStatus() != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only requested returns can be rejected");
        }

        returnEntity.setStatus(ReturnStatus.REJECTED);
        returnEntity.setProcessingNotes(reason);

        Return savedReturn = returnRepository.save(returnEntity);

        // Publish return rejected event
        eventProducer.publishReturnRejected(savedReturn);

        log.info("Return rejected: {}", returnId);

        return mapToResponse(savedReturn, "Return request rejected");
    }

    @Transactional
    public ReturnResponse markAsReceived(String returnId) {
        log.info("Marking return as received: {}", returnId);

        Return returnEntity = getReturnByReturnId(returnId);

        returnEntity.setStatus(ReturnStatus.RECEIVED);
        returnEntity.setReceivedDate(LocalDateTime.now());

        Return savedReturn = returnRepository.save(returnEntity);

        // Start inspection process
        startInspection(savedReturn);

        log.info("Return marked as received: {}", returnId);

        return mapToResponse(savedReturn, "Return received and inspection started");
    }

    @Transactional
    public ReturnResponse processRefund(String returnId) {
        log.info("Processing refund for return: {}", returnId);

        Return returnEntity = getReturnByReturnId(returnId);

        if (returnEntity.getStatus() != ReturnStatus.INSPECTION_PASSED) {
            throw new IllegalStateException("Refund can only be processed after inspection passes");
        }

        // Process refund
        boolean refundSuccessful = refundService.processRefund(
            returnEntity.getOrderId(),
            returnEntity.getCustomerId(),
            returnEntity.getRefundAmount()
        );

        if (refundSuccessful) {
            returnEntity.setStatus(ReturnStatus.REFUNDED);
            returnEntity.setRefundedDate(LocalDateTime.now());

            Return savedReturn = returnRepository.save(returnEntity);

            // Publish refund processed event
            eventProducer.publishRefundProcessed(savedReturn);

            log.info("Refund processed successfully for return: {}", returnId);

            return mapToResponse(savedReturn, "Refund processed successfully");
        } else {
            throw new RuntimeException("Refund processing failed");
        }
    }

    public ReturnResponse getReturnStatus(String returnId) {
        Return returnEntity = getReturnByReturnId(returnId);
        return mapToResponse(returnEntity, "Return details retrieved");
    }

    public List<ReturnResponse> getCustomerReturns(String customerId) {
        List<Return> returns = returnRepository.findByCustomerId(customerId);
        return returns.stream()
            .map(ret -> mapToResponse(ret, ""))
            .collect(Collectors.toList());
    }

    public List<ReturnResponse> getReturnsByStatus(ReturnStatus status) {
        List<Return> returns = returnRepository.findByStatus(status);
        return returns.stream()
            .map(ret -> mapToResponse(ret, ""))
            .collect(Collectors.toList());
    }

    private void validateReturnEligibility(String orderId) {
        // Check if return already exists for this order
        if (returnRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("Return already exists for this order");
        }
        // Additional validation can be added here (e.g., check order date, delivery status)
    }

    private void startInspection(Return returnEntity) {
        // Start automated inspection process
        returnEntity.setStatus(ReturnStatus.INSPECTING);
        returnRepository.save(returnEntity);

        // In a real system, this would trigger an inspection workflow
        // For now, we'll auto-approve after a delay or manual intervention
        log.info("Inspection started for return: {}", returnEntity.getReturnId());
    }

    private Return getReturnByReturnId(String returnId) {
        return returnRepository.findByReturnId(returnId)
            .orElseThrow(() -> new RuntimeException("Return not found: " + returnId));
    }

    private ReturnResponse mapToResponse(Return returnEntity, String message) {
        ReturnResponse response = new ReturnResponse();
        response.setId(returnEntity.getId());
        response.setReturnId(returnEntity.getReturnId());
        response.setOrderId(returnEntity.getOrderId());
        response.setCustomerId(returnEntity.getCustomerId());
        response.setStatus(returnEntity.getStatus());
        response.setRefundAmount(returnEntity.getRefundAmount());
        response.setShippingLabelUrl(returnEntity.getShippingLabelUrl());
        response.setTrackingNumber(returnEntity.getTrackingNumber());
        response.setRequestedDate(returnEntity.getRequestedDate());
        response.setApprovedDate(returnEntity.getApprovedDate());
        response.setRefundedDate(returnEntity.getRefundedDate());
        response.setMessage(message);
        return response;
    }
}

