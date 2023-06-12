package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.parameter.QuestionCategory;
import com.example.newfieldpasser.parameter.QuestionProcess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue
    @Column(name = "question_id")
    private long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "question")
    private Answer answer;

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "question_content")
    private String questionContent;

    @Column(name = "question_category")
    @Enumerated(EnumType.STRING)
    private QuestionCategory questionCategory;

    @Column(name = "question_process")
    @Enumerated(EnumType.STRING)
    private QuestionProcess questionProcess;

    @CreationTimestamp
    @Column(name = "question_register_date")
    private LocalDateTime questionRegisterDate;

    @UpdateTimestamp
    @Column(name = "question_update_date")
    private LocalDateTime questionUpdateDate;

    @Column(name = "question_delete_check")
    private boolean questionDeleteCheck;
}
