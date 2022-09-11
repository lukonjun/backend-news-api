package org.comppress.customnewsapi.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "criteria")
public class CriteriaEntity extends AbstractEntity{
    private String name;
}
