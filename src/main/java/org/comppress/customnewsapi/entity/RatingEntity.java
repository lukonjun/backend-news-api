package org.comppress.customnewsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.comppress.customnewsapi.dto.RatingDto;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Table(name = "rating", indexes = @Index(columnList = "articleId,criteriaId"))
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingEntity extends AbstractEntity{

    private Long criteriaId;
    private Long userId;
    private Long articleId;
    private Integer rating;
    private String guid;

    public RatingDto toDto(){
        RatingDto ratingDto = new RatingDto();
        BeanUtils.copyProperties(this, ratingDto);
        return ratingDto;
    }

}
