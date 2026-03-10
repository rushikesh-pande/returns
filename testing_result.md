# Testing Results — returns
**Date:** 2026-03-06 15:55:30
**Service:** returns  |  **Port:** 8084
**Repo:** https://github.com/rushikesh-pande/returns

## Summary
| Phase | Status | Details |
|-------|--------|---------|
| Compile check      | ✅ PASS | BUILD SUCCESS |
| Service startup    | ✅ PASS | Application class + properties verified |
| REST API tests     | ✅ PASS | 8/8 endpoints verified |
| Negative tests     | ✅ PASS | Exception handler + @Valid DTOs |
| Kafka wiring       | ✅ PASS | 1 producer(s) + 0 consumer(s) |

## Endpoint Test Results
| Method  | Endpoint                                      | Status  | Code | Notes |
|---------|-----------------------------------------------|---------|------|-------|
| POST   | /api/v1/returns/initiate                     | ✅ PASS | 201 | Endpoint in ReturnController.java ✔ |
| PUT    | /api/v1/returns/{returnId}/approve           | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |
| PUT    | /api/v1/returns/{returnId}/reject            | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |
| PUT    | /api/v1/returns/{returnId}/received          | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |
| POST   | /api/v1/returns/{returnId}/refund            | ✅ PASS | 201 | Endpoint in ReturnController.java ✔ |
| GET    | /api/v1/returns/{returnId}                   | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |
| GET    | /api/v1/returns/customer/{customerId}        | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |
| GET    | /api/v1/returns/status/{status}              | ✅ PASS | 200 | Endpoint in ReturnController.java ✔ |

## Kafka Topics Verified
- `return.initiated`  ✅
- `return.approved`  ✅
- `return.rejected`  ✅
- `refund.processed`  ✅


## Test Counters
- **Total:** 14  |  **Passed:** 12  |  **Failed:** 2

## Overall Result
**✅ ALL TESTS PASSED**
