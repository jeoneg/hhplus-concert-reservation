package kr.hhplus.be.server.interfaces.api.concert;

import kr.hhplus.be.server.domain.concert.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.SeatService;
import kr.hhplus.be.server.domain.concert.model.ConcertScheduleInfo;
import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import kr.hhplus.be.server.interfaces.api.concert.response.ConcertScheduleResponse;
import kr.hhplus.be.server.interfaces.api.concert.response.SeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertScheduleService concertScheduleService;
    private final SeatService seatService;

    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<List<ConcertScheduleResponse.GetConcertSchedule>> getSchedules(
            @RequestHeader("Queue-Token") String token,
            @PathVariable Long concertId
    ) {
        List<ConcertScheduleInfo.GetConcertSchedule> schedules = concertScheduleService.getSchedules(concertId);
        List<ConcertScheduleResponse.GetConcertSchedule> response = schedules.stream()
                .map(ConcertScheduleResponse.GetConcertSchedule::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    public ResponseEntity<List<SeatResponse.GetSeat>> getSeats(
            @RequestHeader("Queue-Token") String token,
            @PathVariable Long concertId,
            @PathVariable Long scheduleId
    ) {
        List<SeatInfo.GetSeat> seats = seatService.getSeats(scheduleId);
        List<SeatResponse.GetSeat> response = seats.stream()
                .map(SeatResponse.GetSeat::from)
                .toList();
        return ResponseEntity.ok(response);
    }

}
