package org.comppress.customnewsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Table(name = "rss_feed", indexes = {
        @Index(columnList = "lang")
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class RssFeedEntity extends AbstractEntity{

    @Column(unique = true, nullable = false)
    private String url;
    private Long publisherId;
    private Long categoryId;
    private String lang;

}
