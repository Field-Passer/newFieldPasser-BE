package com.example.newfieldpasser.entity;

import com.example.newfieldpasser.parameter.QuestionCategory;
import com.example.newfieldpasser.parameter.QuestionProcess;
import com.example.newfieldpasser.parameter.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "question")
@SQLDelete(sql = "UPDATE question SET question_delete_check = true WHERE question_id = ?")
@Where(clause = "question_delete_check = false")
public class Question {
    @Id
    @GeneratedValue
    @Column(name = "question_id")
    private long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
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

    public void updateQuestion(String questionTitle, String questionContent,
                               QuestionCategory questionCategory, QuestionProcess questionProcess) {
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionCategory = questionCategory;
        this.questionProcess = questionProcess;
    }

    public void updateQuestionProcess() {
        this.questionProcess = QuestionProcess.COMPLETE_ANSWER;
    }

    public void registerAnswer(Answer answer) {
        this.answer = answer;
    }
}
