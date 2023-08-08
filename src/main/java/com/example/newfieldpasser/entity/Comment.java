package com.example.newfieldpasser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@DynamicUpdate
@Table(name = "comment")
//@SQLDelete(sql = "UPDATE comment SET comment_delete = true WHERE comment_id =?")
//@Where(clause = "comment_delete = false")
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


    @ColumnDefault("FALSE")
    private Boolean deleteCheck;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @CreationTimestamp
    @Column(name = "comment_register_date")
    private LocalDateTime commentRegisterDate;

    @UpdateTimestamp
    @Column(name = "comment_update_date")
    private LocalDateTime commentUpdateDate;


    public void updateComment(String commentContent){

        this.commentContent =commentContent;

    }

    public void updateParent(Comment comment){
        this.parent = comment;
    }

    public void delete(Boolean deleteCheck){
        this.deleteCheck = deleteCheck;
    }
}


