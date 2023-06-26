package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
