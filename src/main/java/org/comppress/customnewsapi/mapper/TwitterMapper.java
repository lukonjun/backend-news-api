package org.comppress.customnewsapi.mapper;

import org.comppress.customnewsapi.dto.TwitterArticleDto;
import org.comppress.customnewsapi.entity.TwitterTweetEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TwitterMapper {

    TwitterArticleDto twitterArticleToTwitterArticleDto(TwitterTweetEntity twitterTweet);

}
