package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Concert extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Builder
    public Concert(Long id, String title) {
        this.id = id;
        this.title = title;
    }

}
