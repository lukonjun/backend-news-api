package org.comppress.customnewsapi.entity;

import lombok.Data;
import org.comppress.customnewsapi.dto.ArticleDto;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Article extends AbstractEntity{

    private String author;
    private String title;
    @Column(length = 65536 * 64)
    private String description;
    @Column(columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT")
    private String urlToImage;
    //@Column(unique = true, columnDefinition = "TEXT", length = 65536 * 3000)
    private String guid;
    private LocalDateTime publishedAt;
    @Column(length = 65536 * 64)
    private String content;
    private Long rssFeedId;
    @Column(columnDefinition = "integer default 0",nullable = false)
    private Integer countRatings = 0;
    private boolean isAccessible = true;
    private boolean isAccessibleUpdated = false;

    public ArticleDto toDto(){
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(this, articleDto);
        return articleDto;
    }

}
