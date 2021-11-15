package org.comppress.customnewsapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.comppress.customnewsapi.utils.Transformer;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity extends AbstractEntity implements Transformer {

    private String name;
    @Column(unique = true)
    private String email;
    private String country;
    private String username;
    private String password;
    private String otp;
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isOtpUsed;
    private String listCategoryIds;
    private String listPublisherIds;

}