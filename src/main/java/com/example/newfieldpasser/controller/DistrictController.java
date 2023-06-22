package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping("/district")
    public ResponseEntity<?> districtInquiry() {
        return districtService.districtInquiry();
    }
}
