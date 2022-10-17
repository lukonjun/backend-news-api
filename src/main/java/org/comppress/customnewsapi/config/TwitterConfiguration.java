package org.comppress.customnewsapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TwitterConfiguration {

    @Value("${twitter.api-key}")
    private String apiKey;
    @Value("${twitter.api-secret}")
    private String apiSecret;
    @Value("${twitter.access-token}")
    private String accessToken;
    @Value("${twitter.access-token-secret}")
    private String accessTokenSecret;

}
