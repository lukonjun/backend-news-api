package org.comppress.customnewsapi.service;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.config.TwitterConfiguration;
import org.comppress.customnewsapi.dto.TwitterArticleDto;
import org.comppress.customnewsapi.dto.article.ArticleDto;
import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.TwitterTweetEntity;
import org.comppress.customnewsapi.mapper.TwitterMapper;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.TwitterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Status;
import twitter4j.v1.User;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TwitterService {

    private final TwitterRepository twitterRepository;
    private final TwitterMapper twitterMapper;
    private final ArticleRepository articleRepository;
    private final TwitterConfiguration twitterConfiguration;


    public ResponseEntity<TwitterArticleDto> getTwitterArticle(Long id) throws TwitterException {

        if(!articleRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        TwitterTweetEntity twitterTweet;
        if((twitterTweet = twitterRepository.findByArticleId(id)) != null){
            TwitterArticleDto twitterArticleDto = twitterMapper.twitterArticleToTwitterArticleDto(twitterTweet);
            return ResponseEntity.status(HttpStatus.OK).body(twitterArticleDto);
        } else {
            var twitter = Twitter.newBuilder()
                    .oAuthConsumer(twitterConfiguration.getApiKey(), twitterConfiguration.getApiSecret())
                    .oAuthAccessToken(twitterConfiguration.getAccessToken(), twitterConfiguration.getAccessTokenSecret())
                    .build();
            Optional<ArticleEntity> article = articleRepository.findById(id);
            Status status = null;
            try {
                // Update status of the Twitter profile
                status = twitter.v1().tweets().updateStatus(article.get().getUrl());
                log.info("Status url: {}", (Object) status.getURLEntities());
            } catch (TwitterException e) {
                throw new RuntimeException("Could not update status of Twitter profile");
            }

            // Save Tweet in the database
            User user = status.getUser();
            String URL = "https://twitter.com/" + user.getScreenName() +"/status/" + status.getId();
            twitterTweet = new TwitterTweetEntity().builder()
                    .articleId(id)
                    .twitterId(user.getId())
                    .twitterArticleUrl(URL)
                    .build();
            twitterRepository.save(twitterTweet);
            TwitterArticleDto twitterArticleDto = twitterMapper.twitterArticleToTwitterArticleDto(twitterTweet);
            return ResponseEntity.status(HttpStatus.OK).body(twitterArticleDto);
        }
    }

    private Twitter getTwitter() {
        /*
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterConfiguration.getApiKey())
                .setOAuthConsumerSecret(twitterConfiguration.getApiSecret())
                .setOAuthAccessToken(twitterConfiguration.getAccessToken())
                .setOAuthAccessTokenSecret(twitterConfiguration.getAccessTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
        */
        return null;
    }


    public void getTweetDetails() {
        twitterRepository.findAll().forEach(t -> {
            if(t.getTwitterId() != null){
                // Get List of Tweets?
                TwitterClient twitterClient = getTwitterClient();
                Tweet tweet  = twitterClient.getTweet(String.valueOf(t.getTwitterId()));
                if(t.getReplyCount() != tweet.getReplyCount()){
                    t.setReplyCount(tweet.getReplyCount());
                    twitterRepository.save(t);
                }
            }
        });
    }

    private TwitterClient getTwitterClient() {
        return new TwitterClient(TwitterCredentials.builder()
                .accessToken(twitterConfiguration.getAccessToken())
                .accessTokenSecret(twitterConfiguration.getAccessTokenSecret())
                .apiKey(twitterConfiguration.getApiKey())
                .apiSecretKey(twitterConfiguration.getApiSecret())
                .build());
    }

    public void setReplyCount(ArticleDto articleDto) {
        TwitterTweetEntity tweet = twitterRepository.findByArticleId(articleDto.getId());
        if(tweet == null){
            articleDto.setCountComment(0);
        }else{
            articleDto.setCountComment(tweet.getReplyCount());
        }
    }
}
