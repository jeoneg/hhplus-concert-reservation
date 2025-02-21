package kr.hhplus.be.server.domain.reservation.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus.INIT;
import static kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus.PUBLISHED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class ReservationOutbox extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String topic;

    @Column(name = "event_key")
    private String key;
    private String message;

    @Enumerated(value = STRING)
    private ReservationOutboxStatus status;

    @Builder
    public ReservationOutbox(Long id, String topic, String key, String message, ReservationOutboxStatus status) {
        this.id = id;
        this.topic = topic;
        this.key = key;
        this.message = message;
        this.status = status;
    }

    public static ReservationOutbox create(String topic, String key, String message) {
        return ReservationOutbox.builder()
            .topic(topic)
            .key(key)
            .message(message)
            .status(INIT)
            .build();
    }

    public void publish() {
        this.status = PUBLISHED;
    }

}
