package org.comppress.customnewsapi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article", indexes = {
        @Index(columnList = "publishedAt"),
        @Index(columnList = "paywallArticle"),
        @Index(columnList = "rssFeedId"),
        @Index(columnList = "countRatings")
})
public class ArticleEntity extends AbstractEntity{

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
    @Column(columnDefinition = "integer default 0",nullable = false)
    private Integer countComment = 0;
    private boolean paywallArticle = false;
    private boolean paywallArticleUpdated = false;
    private boolean scaleImage = false;

}
