package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.TwitterTweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwitterRepository extends JpaRepository<TwitterTweetEntity,Long> {

    TwitterTweetEntity findByArticleId(Long articleId);

}
