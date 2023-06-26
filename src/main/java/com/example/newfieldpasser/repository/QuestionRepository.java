package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Question;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = {"member"})
    Slice<Question> findByMember_MemberId(String memberId , PageRequest pageRequest);

    Optional<Question> findByQuestionId(long questionId);

    void deleteByQuestionId(long questionId);
}
