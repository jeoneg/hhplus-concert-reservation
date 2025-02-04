package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.model.PointInfo;
import kr.hhplus.be.server.interfaces.api.point.request.PointRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping
    public ResponseEntity<PointResponse.GetPoint> getPoint(@RequestParam Long userId) {
        PointInfo.GetPoint point = pointService.getPoint(userId);
        return ResponseEntity.ok(PointResponse.GetPoint.from(point));
    }

    @PostMapping("/charge")
    public ResponseEntity<PointResponse.Charge> chargePoint(@Valid @RequestBody PointRequest.Charge request) {
        PointInfo.Charge point = pointService.charge(request.toCommand());
        return ResponseEntity.ok(PointResponse.Charge.from(point));
    }

}
