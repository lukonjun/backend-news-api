package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.CriteriaDto;
import org.comppress.customnewsapi.service.CriteriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/criteria")
@RequiredArgsConstructor
public class CriteriaController {

    private final CriteriaService criteriaService;

    @GetMapping
    public ResponseEntity<List<CriteriaDto>> getCriteria(){
        return ResponseEntity.ok().body(criteriaService.getCriteria());
    }

}
