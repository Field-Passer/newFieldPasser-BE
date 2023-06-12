package com.example.newfieldpasser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue
    @Column(name = "answer_id")
    private long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "answer_title")
    private String answerTitle;

    @Column(name = "answer_content")
    private String answerContent;

    @CreationTimestamp
    @Column(name = "answer_register_date")
    private LocalDateTime answerRegisterDate;
}
