package org.comppress.customnewsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RssFeed extends AbstractEntity{

    @ManyToOne
    private Category category;
    @Column(unique = true)
    private String urlRssFeed;
    @ManyToOne
    private Publisher publisher;

}
