package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "board")
public class Board {
    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "IMAGE_URL", nullable = false)
    private String imageUrl;

    @Column(name = "TRANSACTION_STATUS")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Column(name = "VIEW_COUNT", nullable = false)
    private Integer viewCount;

    @Column(name = "WISH_COUNT", nullable = false)
    private Integer wishCount;

    @Column(name = "blind")
    private boolean blind;

    @Column(name = "delete_check")
    private boolean deleteCheck;

    @OneToMany(mappedBy = "board")
    private List<Comment> commentList;

    @OneToMany(mappedBy = "board")
    private List<WishBoard> wishBoardList;

    @PrePersist
    public void prePersist() {
        this.viewCount = this.viewCount == null ? 0 : this.viewCount;
        this.wishCount = this.wishCount == null ? 0 : this.wishCount;
    }
}
