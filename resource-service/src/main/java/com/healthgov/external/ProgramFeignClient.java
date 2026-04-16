package com.healthgov.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "program-service"
//    , url = "${program.service.url}"
)
public interface ProgramFeignClient {

    @GetMapping("/programs/{programId}")
    void validateProgramExists(@PathVariable("programId") Long programId);
}