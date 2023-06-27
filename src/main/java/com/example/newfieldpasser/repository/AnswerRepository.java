package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Answer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<Answer> findByAnswerId(long answerId);
}
