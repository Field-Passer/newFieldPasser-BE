package com.example.newfieldpasser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private  Comment parent;


    @OneToMany(mappedBy = "parent",orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @CreationTimestamp
    @Column(name = "comment_register_date")
    private LocalDateTime commentRegisterDate;

    @UpdateTimestamp
    @Column(name = "comment_update_date")
    private LocalDateTime commentUpdateDate;


//    @Formula("(SELECT count(1) FROM reply r WHERE r.comment_Id = comment_Id)")
//    private int replyCount;
//
//    @OneToMany(mappedBy = "comment")
//    private List<Reply> replyList;
    public void updateComment(String commentContent){

        this.commentContent =commentContent;

    }

    public void updateParent(Comment comment){
        this.parent = comment;
    }


}
