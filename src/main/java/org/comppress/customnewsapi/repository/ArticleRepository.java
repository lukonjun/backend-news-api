package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByIsAccessibleUpdatedFalse(Pageable pageable);

    @Query(value = "SELECT * FROM article ORDER BY RAND() LIMIT :numberArticles ", nativeQuery = true)
    List<Article> retrieveRandomArticles(@Param("numberArticles") Integer numberArticles);

    @Query(value = "SELECT * FROM article JOIN rss_feed rf on rf.id = article.rss_feed_id WHERE rf.category_id = :categoryId ", nativeQuery = true)
    List<Article> retrieveArticlesByCategoryId(@Param("categoryId") Long categoryId);

    boolean existsById(Long id);

    Optional<Article> findByGuid(String guid);

    @Query(value = "SELECT * FROM article at JOIN rss_feed rf on rf.id = at.rss_feed_id " +
            "JOIN publisher p on p.id = rf.publisher_id " +
            "JOIN category c on c.id = rf.category_id WHERE " +
            "(:category is null or :category = '' or c.name LIKE %:category%) AND " +
            "(:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%) AND " +
            "(:title is null or :title = '' or at.title LIKE %:title%) AND " +
            "(:language is null or :language = '' or rf.lang LIKE :language) AND " +
            "at.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND " +
            "IFNULL(:toDate,now()) ",
            countQuery = "SELECT count(*) FROM article at JOIN rss_feed rf on rf.id = at.rss_feed_id " +
                    "JOIN publisher p on p.id = rf.publisher_id " +
                    "JOIN category c on c.id = rf.category_id WHERE " +
                    "(:category is null or :category = '' or c.name LIKE %:category%) AND " +
                    "(:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%) AND " +
                    "(:title is null or :title = '' or at.title LIKE %:title%) AND " +
                    "(:language is null or :language = '' or rf.lang LIKE :language) AND " +
                    "at.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND " +
                    "IFNULL(:toDate,now())", nativeQuery = true)
    Page<Article> retrieveByCategoryOrPublisherName(@Param("category") String category,
                                                    @Param("publisherName") String publisherName,
                                                    @Param("title") String title,
                                                    @Param("language") String language,
                                                    @Param("fromDate") LocalDateTime fromDate,
                                                    @Param("toDate") LocalDateTime toDate,
                                                    Pageable pageable);

    @Query(value = "select t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at, a.content, a.count_ratings, a.is_accessible,  t.average_rating_criteria_1, t.average_rating_criteria_2, t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/" +
            "(CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating " +
            "from (SELECT distinct r.article_id, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 " +
            "FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id " +
            "WHERE(:category is null or :category = '' or c.name LIKE %:category%) AND " +
            "(:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%) AND " +
            "(:title is null or :title = '' or a.title LIKE %:title%) AND " +
            "(:language is null or :language = '' or rf.lang LIKE :language) AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC;",countQuery = "select COUNT(*) "+
            "from (SELECT distinct r.article_id, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 " +
            "FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id " +
            "WHERE(:category is null or :category = '' or c.name LIKE %:category%) AND " +
            "(:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%) AND " +
            "(:title is null or :title = '' or a.title LIKE %:title%) AND " +
            "(:language is null or :language = '' or rf.lang LIKE :language) AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC;"
            ,nativeQuery = true)
    Page<CustomRatedArticle> retrieveAllRatedArticlesInDescOrder(
            @Param("category") String category,
            @Param("publisherName") String publisherName,
            @Param("title") String title,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate, Pageable pageable);

    @Query(value = "select t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at, a.content, a.count_ratings, a.is_accessible,  t.average_rating_criteria_1, t.average_rating_criteria_2, t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/" +
            "(CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating " +
            "from (SELECT distinct r.article_id, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 " +
            "FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id " +
            " WHERE rf.category_id = :categoryId AND " +
            "rf.publisher_id in (:publisherIds) AND " +
            "(:language is null or :language = '' or rf.lang LIKE :language) AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC;"
            ,nativeQuery = true)
    List<CustomRatedArticle> retrieveAllRatedArticlesInDescOrder(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    interface CustomRatedArticle {
        Long getPublisher_id();
        Integer getComment_count();
        String getPublisher_name();
        Long getArticle_id();
        String getAuthor();
        String getTitle();
        String getDescription();
        String getUrl();
        String getUrl_to_image();
        String getGuid();
        String getPublished_at();
        String getContent();
        Integer getCount_ratings();
        Boolean getIs_accessible();
        Double getAverage_rating_criteria_1();
        Double getAverage_rating_criteria_2();
        Double getAverage_rating_criteria_3();
        Double getTotal_average_rating();
    }


    @Query(value = "select t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at,\n" +
            "       a.content, a.count_ratings, a.is_accessible, t.average_rating_criteria_1, t.average_rating_criteria_2,\n" +
            "       t.average_rating_criteria_3, sum(t.average_rating_criteria_1  +t.average_rating_criteria_2+  t.average_rating_criteria_3)/\n" +
            "                                    (CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END +\n" +
            "                                     CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END +\n" +
            "                                     CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating\n" +
            "from (SELECT distinct r.article_id,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3\n" +
            "      FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id\n" +
            "WHERE ( rf.lang = :lang ) AND " +
            "c.id in :categoryIds  AND " +
            "p.id in :publisherIds AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC"
            , countQuery = "select count(*) " +
            "from (SELECT distinct r.article_id,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3\n" +
            "      FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id\n" +
            "WHERE ( rf.lang = :lang ) AND " +
            "c.id in :categoryIds  AND " +
            "p.id in :publisherIds AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id"
            , nativeQuery = true)
    Page<CustomRatedArticle> retrieveArticlesByCategoryIdsAndPublisherIdsAndLanguage(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);


    @Query(value = "select p.id as publisher_id, p.name as publisher_name,t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at,\n" +
            "       a.content, a.count_ratings, a.is_accessible, t.average_rating_criteria_1, t.average_rating_criteria_2,\n" +
            "       t.average_rating_criteria_3, sum(t.average_rating_criteria_1  +t.average_rating_criteria_2+  t.average_rating_criteria_3)/\n" +
            "                                    (CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END +\n" +
            "                                     CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END +\n" +
            "                                     CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating\n" +
            "from (SELECT distinct r.article_id,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,\n" +
            "                      (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3\n" +
            "      FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id \n" +
            "WHERE ( rf.lang = :lang ) AND " +
            "c.id = :categoryId AND " +
            "p.id in :publisherIds AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC LIMIT 1"
            , nativeQuery = true)
    CustomRatedArticle retrieveArticlesByCategoryIdsAndPublisherIdsAndLanguageAndLimit(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query(value = "SELECT * FROM article WHERE (:articleId is null or id = :articleId)", nativeQuery = true)
    List<Article> customTestQuery(@Param("articleId") Long articleId);

    @Query(value = "SELECT * FROM article a JOIN rss_feed rf on rf.id = a.rss_feed_id " +
            "WHERE a.count_ratings = 0 AND " +
            "rf.category_id = :categoryId AND " +
            "rf.publisher_id in (:publisherIds) AND " +
            "rf.lang = :lang AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "ORDER BY a.published_at DESC",
            countQuery = "SELECT count(*) FROM article a JOIN rss_feed rf on rf.id = a.rss_feed_id " +
                    "WHERE a.count_ratings = 0 AND " +
                    "rf.category_id = :categoryId AND " +
                    "rf.publisher_id in (:publisherIds) AND " +
                    "rf.lang = :lang AND " +
                    "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())"
            , nativeQuery = true)
    Page<Article> retrieveUnratedArticlesByCategoryIdAndPublisherIdsAndLanguage(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query(value = "select p.id as publisher_id, p.name as publisher_name,t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at,a.content, a.count_ratings, a.is_accessible, t.average_rating_criteria_1, t.average_rating_criteria_2,t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/ (CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating from (SELECT distinct r.article_id,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id INNER JOIN article_topic at2 on a.id = at2.article_id INNER JOIN topic t2 on at2.topic_id = t2.id WHERE rf.lang = :lang AND t2.id = :topicId AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) group by t.article_id order by total_average_rating DESC",
            countQuery = "select COUNT(*) from (SELECT distinct r.article_id,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id INNER JOIN article_topic at2 on a.id = at2.article_id INNER JOIN topic t2 on at2.topic_id = t2.id WHERE rf.lang = :lang AND t2.name = :topic AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) group by t.article_id",
            nativeQuery = true)
    Page<CustomRatedArticle> retrieveRatedArticlesByLangAndTopicId(
            @Param("lang") String lang,
            @Param("topicId") Long topicId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query(value = "Select * from article WHERE id = :id ", nativeQuery = true)
    String[][] test2(
            @Param("id") Long id);


    @Query(value = "select t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at,\n" +
            "                   a.content, a.count_ratings, a.is_accessible, t.average_rating_criteria_1, t.average_rating_criteria_2,\n" +
            "                   t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/\n" +
            "                                                (CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END +\n" +
            "                                                 CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END  +\n" +
            "                                                 CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating\n" +
            "            from (SELECT distinct r.article_id,\n" +
            "                                  (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,\n" +
            "                                  (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,\n" +
            "                                  (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3\n" +
            "                  FROM rating r group by r.article_id) as t INNER JOIN article a ON a.id= t.article_id INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id INNER JOIN category c ON c.id = rf.category_id INNER JOIN publisher p ON p.id = rf.publisher_id  INNER JOIN article_topic t1 on a.id = t1.article_id\n" +
            "             INNER JOIN topic t2 on t1.topic_id = t2.id\n" +
            "            WHERE rf.lang = :lang AND\n" +
            "            t2.name = :topic\n" +
            "            group by t.article_id order by total_average_rating DESC"
            , nativeQuery = true)
    String[][] test1(
            @Param("lang") String lang,
            @Param("topic") String topic);


}
