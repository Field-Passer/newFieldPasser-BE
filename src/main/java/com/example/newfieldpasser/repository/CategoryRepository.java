package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
