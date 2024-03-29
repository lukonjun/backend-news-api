package org.comppress.customnewsapi.service;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.CriteriaDto;
import org.comppress.customnewsapi.entity.CriteriaEntity;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.CriteriaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CriteriaService {

    private final CriteriaRepository criteriaRepository;
    private final MapstructMapper mapstructMapper;

    public List<CriteriaDto> getCriteria() {
        List<CriteriaDto> criteriaDtoList = new ArrayList<>();
        for(CriteriaEntity criteria:criteriaRepository.findAll()){
            criteriaDtoList.add(mapstructMapper.criteriaToCriteriaDto(criteria));
        }
        return criteriaDtoList;
    }
}
