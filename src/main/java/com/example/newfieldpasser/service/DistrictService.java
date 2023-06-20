package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.CategoryDTO;
import com.example.newfieldpasser.dto.DistrictDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final Response response;

    public ResponseEntity<?> districtInquiry() {
        List<DistrictDTO.DistrictResponseDTO> result = districtRepository.findAll()
                .stream()
                .map(DistrictDTO.DistrictResponseDTO::new)
                .collect(Collectors.toList());

        return response.success(result);
    }
}
