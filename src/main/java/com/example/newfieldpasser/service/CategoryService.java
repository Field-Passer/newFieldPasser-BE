package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.CategoryDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Response response;

    public ResponseEntity<?> categoryInquiry() {
        List<CategoryDTO.CategoryResponseDTO> result = categoryRepository.findAll()
                .stream()
                .map(CategoryDTO.CategoryResponseDTO::new)
                .collect(Collectors.toList());

        return response.success(result);
    }

}
