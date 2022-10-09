CREATE VIEW VAverageRatingsEachArticle AS
SELECT distinct r.article_id,
                COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1),0) as average_rating_criteria_1,
                COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2),0) as average_rating_criteria_2,
                COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3),0) as average_rating_criteria_3
FROM rating r group by r.article_id;

CREATE VIEW VTotalAverageRatingEachArticle AS
SELECT v.article_id,
       v.average_rating_criteria_1,
       v.average_rating_criteria_2,
       v.average_rating_criteria_3,
       SUM((v.average_rating_criteria_1 + v.average_rating_criteria_2 + v.average_rating_criteria_3) / (
               CASE WHEN v.average_rating_criteria_1 = 0 THEN 0 ELSE 1 END +
               CASE WHEN v.average_rating_criteria_2 = 0 THEN 0 ELSE 1 END +
               CASE WHEN v.average_rating_criteria_3 = 0 THEN 0 ELSE 1 END
           )) as total_average_rating
from VAverageRatingsEachArticle v GROUP BY article_id order by total_average_rating desc;

CREATE VIEW VGroupByUserIdArticleIdOwnUserRating AS
SELECT v.article_id,
       v.user_id,
       v.average_rating_criteria_1,
       v.average_rating_criteria_2,
       v.average_rating_criteria_3,
       SUM((v.average_rating_criteria_1 + v.average_rating_criteria_2 + v.average_rating_criteria_3) / (
               CASE WHEN v.average_rating_criteria_1 = 0 THEN 0 ELSE 1 END +
               CASE WHEN v.average_rating_criteria_2 = 0 THEN 0 ELSE 1 END +
               CASE WHEN v.average_rating_criteria_3 = 0 THEN 0 ELSE 1 END
           )) as total_average_rating
    from (SELECT distinct r.article_id,
                          r.user_id,
                          COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1),0) as average_rating_criteria_1,
                          COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2),0) as average_rating_criteria_2,
                          COALESCE((select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3),0) as average_rating_criteria_3
FROM rating r group by r.article_id, r.user_id) as v GROUP BY article_id, user_id order by total_average_rating desc;
