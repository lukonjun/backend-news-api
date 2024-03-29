package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.article.CustomArticle;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Page<ArticleEntity> findByPaywallArticleUpdatedFalse(Pageable pageable);

    boolean existsById(Long id);

    Optional<ArticleEntity> findByGuid(String guid);

    @Query(value = "SELECT * FROM article ORDER BY RAND() LIMIT :numberArticles ", nativeQuery = true)
    List<ArticleEntity> retrieveRandomArticles(@Param("numberArticles") Integer numberArticles);

    @Query(value = "SELECT * FROM article WHERE date_created >= NOW() - INTERVAL 1 DAY ORDER BY RAND() LIMIT 1", nativeQuery = true)
    ArticleEntity retrieveOneRandomArticleIntervalOneDay();

    @Query(value = """
            Select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId, 
                   c.name as categoryName, 
                   p.name as publisherName
            from article a
                JOIN rss_feed rf on rf.id = a.rss_feed_id
                JOIN category c on c.id = rf.category_id
                JOIN publisher p on p.id = rf.publisher_id
            WHERE rf.category_id = :categoryId
              AND p.id in :publisherIds
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
              AND a.published_at is not null Order by a.published_at DESC Limit 1
            """, nativeQuery = true)
    CustomArticle retrieveLatestArticleOfCategory(
            @Param("categoryId") Long categoryId,
            @Param("language") String language,
            @Param("filterOutPaywallArticles") Boolean filterOutPaywallArticles,
            @Param("publisherIds") List<Long> publisherIds
    );


    @Query(value = """
            Select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName
            from article a
                     JOIN rss_feed rf on rf.id = a.rss_feed_id
                     JOIN category c on c.id = rf.category_id
                     JOIN publisher p on p.id = rf.publisher_id
            WHERE (:category is null or :category = '' or c.name LIKE %:category%) 
              AND (:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%)
              AND (:title is null or :title = '' or a.title LIKE %:title%)
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            """,
            countQuery = """
                    Select count(*) from article a
                             JOIN rss_feed rf on rf.id = a.rss_feed_id
                             JOIN category c on c.id = rf.category_id
                             JOIN publisher p on p.id = rf.publisher_id
                    WHERE (:category is null or :category = '' or c.name LIKE %:category%)
                      AND (:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%)
                      AND (:title is null or :title = '' or a.title LIKE %:title%)
                      AND (:language is null or :language = '' or rf.lang LIKE :language)
                      AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
                      AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
                    """, nativeQuery = true)
    Page<CustomArticle> retrieveByCategoryOrPublisherNameToCustomArticle(
            @Param("category") String category,
            @Param("publisherName") String publisherName,
            @Param("title") String title,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("filterOutPaywallArticles") Boolean filterOutPaywallArticles,
            Pageable pageable);

    @Query(value = """
            select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName,
                   v.average_rating_criteria_1 as averageRatingCriteria1,
                   v.average_rating_criteria_2 as averageRatingCriteria2,
                   v.average_rating_criteria_3 as averageRatingCriteria3,
                   v.total_average_rating as totalAverageRating
            FROM VTotalAverageRatingEachArticle v
                   INNER JOIN article a ON a.id= v.article_id
                   INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id
                   INNER JOIN category c ON c.id = rf.category_id
                   INNER JOIN publisher p ON p.id = rf.publisher_id
            WHERE rf.category_id = :categoryId
              AND rf.publisher_id in (:publisherIds)
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
              AND a.published_at BETWEEN IFNULL(:fromDate, NOW() - INTERVAL 1 DAY) AND IFNULL(:toDate,now())
            group by v.article_id order by totalAverageRating DESC, a.published_at DESC
            """, nativeQuery = true)
    List<CustomRatedArticle> retrieveAllRatedArticlesInDescOrder(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("filterOutPaywallArticles") Boolean filterOutPaywallArticles);


    @Query(value = """
            select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName,
                   v.average_rating_criteria_1 as averageRatingCriteria1,
                   v.average_rating_criteria_2 as averageRatingCriteria2,
                   v.average_rating_criteria_3 as averageRatingCriteria3,
                   v.total_average_rating as totalAverageRating
            FROM VTotalAverageRatingEachArticle v
                   INNER JOIN article a ON a.id = v.article_id
                   INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id
                   INNER JOIN category c ON c.id = rf.category_id
                   INNER JOIN publisher p ON p.id = rf.publisher_id
           WHERE ( rf.lang = :lang )
                         AND c.id = :categoryId
                         AND p.id in :publisherIds
                         AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
                         AND a.published_at BETWEEN IFNULL(:fromDate, NOW() - INTERVAL 1 DAY) AND IFNULL(:toDate,now())
        group by v.article_id order by totalAverageRating DESC, a.published_at DESC LIMIT 1
                        """, nativeQuery = true)
    CustomRatedArticle retrieveOneRatedArticleByCategoryIdsAndPublisherIdsAndLanguageAndLimit(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("filterOutPaywallArticles") Boolean filterOutPaywallArticles
    );

    @Query(value = """
            Select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName
            from article a
                     JOIN rss_feed rf on rf.id = a.rss_feed_id
                     JOIN category c on c.id = rf.category_id
                     JOIN publisher p on p.id = rf.publisher_id
            WHERE a.count_ratings = 0
              AND rf.category_id = :categoryId
              AND rf.publisher_id in (:publisherIds)
              AND rf.lang = :lang
              AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            ORDER BY a.published_at DESC
            """, countQuery = """
            Select count(*)
            from article a
                     JOIN rss_feed rf on rf.id = a.rss_feed_id
                     JOIN category c on c.id = rf.category_id
                     JOIN publisher p on p.id = rf.publisher_id
            WHERE a.count_ratings = 0
              AND rf.category_id = :categoryId
              AND rf.publisher_id in (:publisherIds)
              AND rf.lang = :lang
              AND (:filterOutPaywallArticles = FALSE or a.paywall_article = FALSE)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            """, nativeQuery = true)
    Page<CustomArticle> retrieveUnratedArticlesByCategoryIdAndPublisherIdsAndLanguage(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("filterOutPaywallArticles") Boolean filterOutPaywallArticles,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query(value = """
            select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.paywall_article as paywallArticle, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName,
                   v.average_rating_criteria_1 as averageRatingCriteria1,
                   v.average_rating_criteria_2 as averageRatingCriteria2,
                   v.average_rating_criteria_3 as averageRatingCriteria3,
                   v.total_average_rating as totalAverageRating
            FROM VGroupByUserIdArticleIdOwnUserRating v
                   INNER JOIN article a ON a.id = v.article_id
                   INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id
                   INNER JOIN category c ON c.id = rf.category_id
                   INNER JOIN publisher p ON p.id = rf.publisher_id
            WHERE count_ratings > 0 
              AND v.user_id = :userId 
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) 
            group by v.article_id, v.user_id order by totalAverageRating DESC;
            """,nativeQuery = true)
    List<CustomRatedArticle> getRatedArticleFromUser(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
