package kr.hhplus.be.server.interfaces.api.payment;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentFacade paymentFacade;

    @PostMapping
    public ResponseEntity<PaymentResponse.Create> payment(
            @RequestHeader("Queue-Token") String token,
            @Valid @RequestBody PaymentRequest.Create request
    ) {
        PaymentResult.Create result = paymentFacade.payment(request.toInput(token));
        return ResponseEntity.status(CREATED).body(PaymentResponse.Create.from(result));
    }
    
}
