package org.comppress.customnewsapi.service.twitter;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import org.comppress.customnewsapi.dto.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.TwitterArticleDto;
import org.comppress.customnewsapi.entity.Article;
import org.comppress.customnewsapi.entity.TwitterTweet;
import org.comppress.customnewsapi.mapper.TwitterMapper;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.TwitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.Optional;

@Service
public class TwitterServiceImpl implements TwitterService {

    private final TwitterRepository twitterRepository;
    private final TwitterMapper twitterMapper;
    private final ArticleRepository articleRepository;
    private final Twitter twitter;
    private final TwitterClient twitterClient;

    static String API_KEY = "3OcPF9Kg5bsCR1eYtMz4hixM1";
    static String API_SECRET = "Q2LZCxGna2ckmMNtJW0f3eKhBRmtkMRhPOv50mVSBsGZKZGBb7";
    static String ACCESS_TOKEN = "1444941818047700993-9PGTpStoWYUJDy5YIE1Sz1g9GKWDLc";
    static String ACCESS_TOKEN_SECRET = "cwsR23PMMqa5XS3mzlU0G2oTtGzqIDps4V6tsjBn3kbpO";

    @Autowired
    public TwitterServiceImpl(TwitterRepository twitterRepository, TwitterMapper twitterMapper, ArticleRepository articleRepository, Twitter twitter, TwitterClient twitterClient) {
        this.twitterRepository = twitterRepository;
        this.twitterMapper = twitterMapper;
        this.articleRepository = articleRepository;
        this.twitter = twitter;
        this.twitterClient = twitterClient;
    }

    @Override
    public ResponseEntity<TwitterArticleDto> getTwitterArticle(Long id) {

        TwitterTweet twitterTweet = null;
        if((twitterTweet = twitterRepository.findByArticleId(id)) != null){
            TwitterArticleDto twitterArticleDto = twitterMapper.twitterArticleToTwitterArticleDto(twitterTweet);
            return ResponseEntity.status(HttpStatus.OK).body(twitterArticleDto);
        } else {
            // https://developer.twitter.com/en/docs/twitter-api/v1/tweets/post-and-engage/api-reference/post-statuses-update
            // https://vhudyma-blog.eu/post-a-tweet-using-twitter-api/ (Java Script)
            // https://github.com/twitterdev/Twitter-API-v2-sample-code
            // https://vasisouv.github.io/twitter-api-tutorial/tutorial.html
            // Create Article against Twitter API

            Twitter twitterApi = twitter;
            Optional<Article> article = articleRepository.findById(id);
            Status status = null;
            try {
                status = twitterApi.updateStatus("Hello Test URL " + article.get().getUrl());
            } catch (TwitterException e) {
                // Thorw Custom Exception
                e.printStackTrace();
                throw new RuntimeException("Could not Update Status (Tweet)");
            }
            System.out.println(status.getURLEntities());
            // Save new Twitter Article to Database
            // Build URL String to access Tweet
            // https://twitter.com/justano12715638
            User user = status.getUser();
            String URL = "https://twitter.com/" + user.getScreenName() +"/status/" + status.getId();
            System.out.println(URL);
            twitterTweet = new TwitterTweet().builder()
                    .articleId(id)
                    .twitterId(status.getId())
                    .twitterArticleUrl(URL)
                    .build();
            twitterRepository.save(twitterTweet);
            TwitterArticleDto twitterArticleDto = twitterMapper.twitterArticleToTwitterArticleDto(twitterTweet);
            return ResponseEntity.status(HttpStatus.OK).body(twitterArticleDto);
        }
    }

    @Override
    public void getTweetDetails() {
        twitterRepository.findAll().forEach(t -> {
            if(t.getTwitterId() != null){
                // Get List of Tweets?
                Tweet tweet  = twitterClient.getTweet(String.valueOf(t.getTwitterId()));
                if(t.getReplyCount() != tweet.getReplyCount()){
                    t.setReplyCount(tweet.getReplyCount());
                    twitterRepository.save(t);
                }
            }
        });
    }

    @Override
    public void setReplyCount(CustomRatedArticleDto articleDto) {
        TwitterTweet tweet = twitterRepository.findByArticleId(articleDto.getArticle_id());
        if(tweet == null){
            articleDto.setCount_comment(0);
        }else{
            articleDto.setCount_comment(tweet.getReplyCount());
        }
    }
}
