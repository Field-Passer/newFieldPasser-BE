package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@DynamicUpdate
@Table(name = "board")
@SQLDelete(sql = "UPDATE board SET delete_check = true, delete_date = now() WHERE board_id = ?")
@Where(clause = "delete_check = false AND blind = false")
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TRANSACTION_STATUS", nullable = false)
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

    public void updatePost(Category category, District district, String imageUrl,
                           String title, String content, LocalDateTime startTime,
                           LocalDateTime endTime, TransactionStatus transactionStatus, int price) {
        this.category = category;
        this.district = district;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transactionStatus = transactionStatus;
        this.price = price;
    }

    public void blindBoard() {
        this.blind = !this.blind;
    }
}
