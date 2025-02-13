package kr.hhplus.be.server.interfaces.api.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.request.ReservationRequest;
import kr.hhplus.be.server.interfaces.api.reservation.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse.Create> reserve(
        @RequestHeader("Queue-Token") String token,
        @Valid @RequestBody ReservationRequest.Create request
    ) {
        ReservationInfo.Create reserve = reservationService.reserve(request.toCommand());
        return ResponseEntity.status(CREATED).body(ReservationResponse.Create.from(reserve));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse.Search>> search(
        @RequestHeader("Queue-Token") String token,
        @RequestParam String status,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return ResponseEntity.ok(reservationService.search(ReservationStatus.valueOf(status), startDate, endDate));
    }

}
