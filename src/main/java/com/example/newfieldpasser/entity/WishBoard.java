package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.parameter.WishBoardId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(WishBoardId.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wishboard")
public class WishBoard {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @CreationTimestamp
    @Column(name = "wish_register_date")
    private LocalDateTime registerDate;
}
