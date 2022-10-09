package org.comppress.customnewsapi.service;


import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.MediaModule;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.ParsingFeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.dto.article.CustomArticleDto;
import org.comppress.customnewsapi.dto.article.CustomRatedArticleDto;
import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.PublisherEntity;
import org.comppress.customnewsapi.entity.RssFeedEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.entity.article.CustomArticle;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
import org.comppress.customnewsapi.exceptions.AuthenticationException;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.*;
import org.comppress.customnewsapi.utils.CustomStringUtils;
import org.comppress.customnewsapi.utils.DateUtils;
import org.comppress.customnewsapi.utils.PageHolderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    @Value("${image.width}")
    private Integer imageWidth;

    @Value("${image.height}")
    private Integer imageHeight;

    private final RssFeedRepository rssFeedRepository;
    private final ArticleRepository articleRepository;
    private final PublisherRepository publisherRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final MapstructMapper mapstructMapper;

    public void fetchArticlesFromRssFeeds() {

        for (RssFeedEntity rssFeed : rssFeedRepository.findAll()) {
            SyndFeed feed;
            try {
                URL feedSource = new URL(rssFeed.getUrl());
                HttpURLConnection conn = (HttpURLConnection) feedSource.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                // System.out.println(sb);

                SyndFeedInput input = new SyndFeedInput();
                feed = input.build(new XmlReader(feedSource));
                log.info("Fetching News from " + rssFeed.getUrl());
            } catch (ParsingFeedException e) {
                log.error("Feed can not be parsed, please recheck the url " + rssFeed.getUrl());
                e.printStackTrace();
                continue;
            } catch (FileNotFoundException e) {
                log.error("FileNotFoundException most likely a dead link, check the url " + rssFeed.getUrl());
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            log.info("Feed of size " + feed.getEntries().size());
            saveArticle(rssFeed, feed);
        }
    }

    private void saveArticle(RssFeedEntity rssFeed, SyndFeed feed) {
        for (SyndEntry syndEntry : feed.getEntries()) {
            ArticleEntity article = customMappingSyndEntryImplToArticle(syndEntry, rssFeed);
            if (articleRepository.findByGuid(article.getGuid()).isPresent()) continue;
            try {
                articleRepository.save(article);
            } catch (DataIntegrityViolationException e) {
                log.error("Duplicate Record found while saving data {}", e.getLocalizedMessage());
            } catch (Exception e) {
                log.error("Error while saving data {}", e.getLocalizedMessage());
            }
        }
    }

    @Async("ThreadPoolExecutor")
    public void update(ArticleEntity article) {
        try {
            String response = urlReader(article.getUrl());
            if (response != null) {
                if (response.contains("\"isAccessibleForFree\":false") || response.contains("\"isAccessibleForFree\": false")) {
                    article.setPaywallArticle(true);
                } else {
                    article.setPaywallArticle(false);
                }
            }
            article.setPaywallArticleUpdated(true);
            articleRepository.save(article);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public boolean checkIfArticleIsAccessibleWithoutPaywall(String response){
        if (response.contains("\"isAccessibleForFree\":false") || response.contains("\"isAccessibleForFree\": false")) {
            return false;
        } else {
            return true;
        }
    }

    public String formatText(String text) {
        //TODO ENHANCE, FILTER ALSO FOR HTML TAGS LIKE <p>
        //String title = Text.normalizeString(text);
        text = text.replace("\n", "");
        text = text.replace("\t", "");
        text = text.replace("\r", "");
        return text;
    }

    public ArticleEntity customMappingSyndEntryImplToArticle(SyndEntry syndEntry, RssFeedEntity rssFeed) {
        ArticleEntity article = new ArticleEntity();
        if (syndEntry.getAuthor() != null) {
            article.setAuthor(syndEntry.getAuthor());
        }
        if (syndEntry.getTitle() != null) {
            article.setTitle(formatText(syndEntry.getTitle()));
        }
        if (syndEntry.getLink() != null) {
            article.setUrl(syndEntry.getLink());
        }
        if (syndEntry.getEnclosures() != null && !syndEntry.getEnclosures().isEmpty()) {
            article.setUrlToImage(syndEntry.getEnclosures().get(0).getUrl());

            MediaEntryModule mm = (MediaEntryModule) syndEntry.getModule(MediaModule.URI);
            if(mm != null && mm.getMediaContents() != null && mm.getMediaContents().length != 0){
                //log.info("{}", mm.getMediaContents()[0].getReference());
                article.setUrlToImage(mm.getMediaContents()[0].getReference().toString());
            }
        } else {
            String imgUrl = null;
            if (syndEntry.getDescription() != null) {
                imgUrl = CustomStringUtils.getImgLinkFromTagSrc(syndEntry.getDescription().getValue(), "src=\"");
            }
            if (imgUrl == null && syndEntry.getContents() != null && syndEntry.getContents().size() > 0) {
                imgUrl = CustomStringUtils.getImgLinkFromTagSrc(syndEntry.getContents().get(0).getValue(), "src=\"");
            }
            if (imgUrl == null && syndEntry.getContents() != null && syndEntry.getContents().size() > 0) {
                imgUrl = CustomStringUtils.getImgLinkFromTagSrc(syndEntry.getContents().get(0).getValue(), "url=\"");
            }

            Dimension dimension = new Dimension(0, 0);

            if (imgUrl != null) {
                dimension = getImageDimension(imgUrl);
            }

            boolean isBadResolution = false;
            if(dimension != null){
                // If width or length of the image is less than 200px then we save the publisher image
                if ((dimension.getHeight() < imageHeight || dimension.getWidth() < imageWidth) && imgUrl != null) {
                    isBadResolution = true;
                    log.debug("Picture with image url {} has a bad resolution", imgUrl);
                }
            }

            if(rssFeed != null){
                if (imgUrl == null || imgUrl.isEmpty() || isBadResolution) {
                    Optional<PublisherEntity> publisher = publisherRepository.findById(rssFeed.getPublisherId());
                    article.setUrlToImage(publisher.get().getUrlToImage());
                    article.setScaleImage(true);
                } else {
                    article.setUrlToImage(imgUrl);
                }
            }

            MediaEntryModule mm = (MediaEntryModule) syndEntry.getModule(MediaModule.URI);
            if(mm != null && mm.getMediaContents() != null && mm.getMediaContents().length != 0){
                // log.info("{}", mm.getMediaContents()[0].getReference());
                article.setUrlToImage(mm.getMediaContents()[0].getReference().toString());
                article.setScaleImage(false);
            }

            if(article.getUrlToImage() == null || article.getUrlToImage().equals("") || article.getUrlToImage() == null){
                log.debug("ImgUrl is in undesired State");
            }

        }
        if (syndEntry.getUri() != null) {
            article.setGuid(syndEntry.getUri());
        }
        if (syndEntry.getPublishedDate() != null) {
            syndEntry.getPublishedDate();
            article.setPublishedAt(syndEntry.getPublishedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        if (syndEntry.getContents() != null && !syndEntry.getContents().isEmpty()) {
            article.setContent(formatText(syndEntry.getContents().get(0).getValue()));
        }
        if (syndEntry.getDescription() != null) {
            article.setDescription(formatText(syndEntry.getDescription().getValue()));
        }

        if (rssFeed != null) {
            article.setRssFeedId(rssFeed.getId());
        } else {
            article.setRssFeedId(-1L);
        }
        return article;
    }

    private String urlReader(String url) throws URISyntaxException, IOException, InterruptedException {
        log.info("Send GET request to " + url);
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Accept-Encoding", "identity")
                    .GET()
                    .build();
        }catch (Exception e){
            log.info("Exception in Thread");
            log.info(e.getMessage());
            return null;
        }

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        var response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public ResponseEntity<GenericPage<CustomArticleDto>> getArticles(int page, int size, String title, String category, String publisherNewsPaper, String lang, Boolean filterOutPaywallArticles, String fromDate, String toDate) {

        Page<CustomArticle> articlesPage = articleRepository
                .retrieveByCategoryOrPublisherNameToCustomArticle(category,
                        publisherNewsPaper, title, lang,
                        DateUtils.stringToLocalDateTime(fromDate), DateUtils.stringToLocalDateTime(toDate), filterOutPaywallArticles,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        GenericPage<CustomArticleDto> genericPage = new GenericPage<>();
        genericPage.setData(articlesPage.stream().map(mapstructMapper::customArticleToCustomArticleDto).collect(Collectors.toList()));

        // TODO Rewrite this with MapStruct instead of copyProperties?
        BeanUtils.copyProperties(articlesPage, genericPage);

        return ResponseEntity.status(HttpStatus.OK).body(genericPage);

    }

    public ResponseEntity<GenericPage> getRatedArticles(int page, int size, Long categoryId,
                                                        List<Long> listPublisherIds, String lang,
                                                        String fromDate, String toDate, Boolean filterOutPaywallArticles, String guid) throws AuthenticationException {

        //log.info("Request Parameter for /custom-news-api/articles/rated: ");
        //log.info("page: {}, size: {}, categoryId: {}, listPublisherIds: {}, lang: {}, fromDate: {}, toDate: {}, filterOutPaywallArticles: {}, guid: {}", page, size, categoryId, listPublisherIds, lang,
        //         fromDate, toDate, filterOutPaywallArticles, guid);

        UserEntity userEntity = null;
        if (guid == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());
            if(userEntity == null) throw new AuthenticationException("You are not authorized, please login","");
        }

        if (listPublisherIds == null) {
            listPublisherIds = publisherRepository.findAll().stream().map(PublisherEntity::getId).collect(Collectors.toList());
        }
        List<CustomRatedArticle> customRatedArticleList = articleRepository.retrieveAllRatedArticlesInDescOrder(
                categoryId, listPublisherIds, lang,
                DateUtils.stringToLocalDateTime(fromDate), DateUtils.stringToLocalDateTime(toDate), filterOutPaywallArticles);
        /* // TODO WHY CAN WE NOT USE THE COUNT QUERY HERE; SQL ERROR
        Page<ArticleRepository.CustomRatedArticle> customRatedArticlePage = articleRepository.retrieveAllRatedArticlesInDescOrder(
                title, category, publisherNewsPaper, lang,
                DateUtils.stringToLocalDateTime(fromDate), DateUtils.stringToLocalDateTime(toDate),PageRequest.of(page,size));
        getCustomRatedArticleDtoGenericPage(customRatedArticlePage)
        */
        List<CustomRatedArticleDto> customRatedArticleDtoList = new ArrayList<>();
        customRatedArticleList.forEach(customRatedArticle -> {
            customRatedArticleDtoList.add(mapstructMapper.customRatedArticleToCustomRatedArticleDto(customRatedArticle));
        });

        // Check if has been rated by user
        for(CustomRatedArticleDto articleDto:customRatedArticleDtoList){
            if(userEntity != null){
                if(!ratingRepository.findByUserIdAndArticleId(userEntity.getId(),articleDto.getId()).isEmpty()){
                    articleDto.setIsRatedByUser(true);
                } else {
                    articleDto.setIsRatedByUser(false);
                }
            }else{
                if(!ratingRepository.findByGuidAndArticleId(guid, articleDto.getId()).isEmpty()){
                    articleDto.setIsRatedByUser(true);
                } else {
                    articleDto.setIsRatedByUser(false);
                }
            }
        }

        return PageHolderUtils.getResponseEntityGenericPage(page, size, customRatedArticleDtoList);
    }

    public ResponseEntity<GenericPage<CustomArticleDto>> getArticlesNotRated(int page, int size, Long categoryId, List<Long> listPublisherIds, String lang, Boolean filterOutPaywallArticles, String fromDate, String toDate) {
        if (listPublisherIds == null) {
            listPublisherIds = publisherRepository.findAll().stream().map(PublisherEntity::getId).collect(Collectors.toList());
        }
        Page<CustomArticle> articlesPage = articleRepository.retrieveUnratedArticlesByCategoryIdAndPublisherIdsAndLanguage(categoryId, listPublisherIds, lang, filterOutPaywallArticles, DateUtils.stringToLocalDateTime(fromDate), DateUtils.stringToLocalDateTime(toDate),PageRequest.of(page, size));

        GenericPage<CustomArticleDto> genericPage = new GenericPage<>();
        genericPage.setData(articlesPage.stream().map(mapstructMapper::customArticleToCustomArticleDto).collect(Collectors.toList()));
        BeanUtils.copyProperties(articlesPage, genericPage);

        return ResponseEntity.status(HttpStatus.OK).body(genericPage);

    }

    public ResponseEntity<GenericPage> getRatedArticlesFromUser(int page, int size, String fromDate, String toDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        List<CustomRatedArticle> articleList = articleRepository.getRatedArticleFromUser(
                userEntity.getId(),
                DateUtils.stringToLocalDateTime(fromDate),
                DateUtils.stringToLocalDateTime(toDate));

        List<CustomRatedArticleDto> customRatedArticleDtoList = articleList.stream().map(mapstructMapper::customRatedArticleToCustomRatedArticleDto).collect(Collectors.toList());

        return PageHolderUtils.getResponseEntityGenericPage(page, size, customRatedArticleDtoList);
    }

    private Dimension getImageDimension(String imageUrl) {

        BufferedImage image;
        URL url = null;
        try {
            url = new URL(imageUrl);
            image = ImageIO.read(url);

            if(image == null)return null;
//            TODO size checking

//            DataBuffer dataBuffer = image.getData().getDataBuffer();
//            long sizeBytes = ((long) dataBuffer.getSize()) * 4L;
//            long sizeMB = sizeBytes / (1024L * 1024L);

            return new Dimension(image.getHeight(), image.getWidth());

        } catch (IOException e) {
            log.error("Error while get the resolution of the image {}",url);
            return null;
        }

    }
}
