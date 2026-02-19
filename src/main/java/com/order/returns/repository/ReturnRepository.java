package com.order.returns.repository;

import com.order.returns.model.Return;
import com.order.returns.model.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

    Optional<Return> findByReturnId(String returnId);

    List<Return> findByOrderId(String orderId);

    List<Return> findByCustomerId(String customerId);

    List<Return> findByStatus(ReturnStatus status);

    List<Return> findByCustomerIdAndStatus(String customerId, ReturnStatus status);

    boolean existsByOrderId(String orderId);
}

