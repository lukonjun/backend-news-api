package org.comppress.customnewsapi.dto.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomRatedArticleDto extends CustomArticleDto {

    @JsonProperty(value = "average_rating_criteria_1")
    private Double averageRatingCriteria1;
    @JsonProperty(value = "average_rating_criteria_2")
    private Double averageRatingCriteria2;
    @JsonProperty(value = "average_rating_criteria_3")
    private Double averageRatingCriteria3;
    @JsonProperty(value = "total_average_rating")
    private Double totalAverageRating;
}
