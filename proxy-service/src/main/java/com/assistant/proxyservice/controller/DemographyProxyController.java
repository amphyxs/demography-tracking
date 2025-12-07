package com.assistant.proxyservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.DemographyProxyService;

@RestController
@RequestMapping("/api/proxy/demography")
@RequiredArgsConstructor
@Slf4j
public class DemographyProxyController {

    private final DemographyProxyService proxyService;

    @GetMapping("/hair-color/{hairColor}")
    public ResponseEntity<Long> getCountByHairColor(@PathVariable String hairColor) {
        log.info("Proxy request: Getting count by hair color: {}", hairColor);

        try {
            Long result = proxyService.getCountByHairColor(hairColor);

            log.info("Proxy response: Count for hair color {} is {}", hairColor, result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error in proxy request for hair color {}: {}", hairColor, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/nationality/{nationality}/eye-color/{eyeColor}/percentage")
    public ResponseEntity<Double> getPercentageByNationalityAndEyeColor(
            @PathVariable String nationality,
            @PathVariable String eyeColor) {

        log.info("Proxy request: Getting percentage by nationality: {} and eye color: {}", nationality, eyeColor);

        try {
            Double result = proxyService.getPercentageByNationalityAndEyeColor(nationality, eyeColor);

            log.info("Proxy response: Percentage for nationality {} and eye color {} is {}%", nationality, eyeColor, result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error in proxy request for nationality {} and eye color {}: {}", nationality, eyeColor, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}