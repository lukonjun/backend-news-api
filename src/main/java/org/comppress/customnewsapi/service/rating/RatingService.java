package org.comppress.customnewsapi.service.rating;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.CriteriaRatingDto;
import org.comppress.customnewsapi.dto.RatingDto;
import org.comppress.customnewsapi.dto.SubmitRatingDto;
import org.comppress.customnewsapi.dto.response.CreateRatingResponseDto;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.dto.response.UpdateRatingResponseDto;
import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.CriteriaEntity;
import org.comppress.customnewsapi.entity.RatingEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.exceptions.ArticleDoesNotExistException;
import org.comppress.customnewsapi.exceptions.AuthenticationException;
import org.comppress.customnewsapi.exceptions.CriteriaDoesNotExistException;
import org.comppress.customnewsapi.exceptions.RatingIsInvalidException;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.CriteriaRepository;
import org.comppress.customnewsapi.repository.RatingRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.security.AppAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CriteriaRepository criteriaRepository;
    private final ArticleRepository articleRepository;
    private final AppAuthenticationService appAuthenticationService;

    public List<RatingDto> getRatings() {
        List<RatingEntity> ratingList = ratingRepository.findAll();
        List<RatingDto> ratingDtoList = new ArrayList<>();
        ratingList.stream().map(rating -> ratingDtoList.add(rating.toDto()));
        return ratingDtoList;
    }

    public ResponseEntity<ResponseDto> submitRating(SubmitRatingDto submitRatingDto, String guid) throws Exception, RatingIsInvalidException {
        UserEntity userEntity = null;
        if (guid == null) {
            String currentUserName = appAuthenticationService.getCurrentUsername();
            userEntity = userRepository.findByUsernameAndDeletedFalse(currentUserName);
            if(userEntity == null) throw new AuthenticationException("You are not authorized, please login","");
        }

        List<RatingEntity> ratings = new ArrayList<>();
        boolean shouldUpdateRating = false;
        if (userEntity == null && guid != null) {
            // NOT LOGGED IN USER WITH GUID
            for (CriteriaRatingDto criteriaRating : submitRatingDto.getRatings()) {
                checkIfRatingInValidRange(criteriaRating);
                validateArticleAndCriteria(submitRatingDto, criteriaRating);
                RatingEntity rating = ratingRepository.findByGuidAndArticleIdAndCriteriaId(guid,
                        submitRatingDto.getArticleId(),
                        criteriaRating.getCriteriaId());
                shouldUpdateRating = prepareRating(shouldUpdateRating,submitRatingDto, guid, ratings, criteriaRating, rating, 0L);
            }
        } else {
            // LOGGED IN USER WITH JWT
            for (CriteriaRatingDto criteriaRating : submitRatingDto.getRatings()) {
                checkIfRatingInValidRange(criteriaRating);
                validateArticleAndCriteria(submitRatingDto, criteriaRating);
                RatingEntity rating = ratingRepository.findByUserIdAndArticleIdAndCriteriaId(userEntity.getId(),
                        submitRatingDto.getArticleId(),
                        criteriaRating.getCriteriaId());
                shouldUpdateRating = prepareRating(shouldUpdateRating,submitRatingDto, "-", ratings, criteriaRating, rating, userEntity.getId());
            }
        }
        ratingRepository.saveAll(ratings);
        if(shouldUpdateRating){
            Optional<ArticleEntity> article = articleRepository.findById(submitRatingDto.getArticleId());
            if (article.isPresent()) {
                Integer countRatings = article.get().getCountRatings();
                if (countRatings == null) countRatings = 0;
                countRatings = countRatings + 1;
                article.get().setCountRatings(countRatings);
                articleRepository.save(article.get());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(UpdateRatingResponseDto.builder()
                    .message("Updated rating for article")
                    .submitRatingDto(submitRatingDto)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateRatingResponseDto.builder()
                    .message("Created rating for article")
                    .submitRatingDto(submitRatingDto)
                    .build());
        }
    }

    private void checkIfRatingInValidRange(CriteriaRatingDto ratingDto) throws RatingIsInvalidException {
        if(ratingDto.getRating() < 1 || ratingDto.getRating() > 5) {
            throw new RatingIsInvalidException(String.format("invalid rating: %s (valid values are 1 - 5)",ratingDto.getRating()), String.valueOf(ratingDto.getRating()));
        }
    }

    private boolean prepareRating(boolean isUpdateRating, SubmitRatingDto submitRatingDto, String guid, List<RatingEntity> ratings, CriteriaRatingDto criteriaRating, RatingEntity rating, long l) {
        if (rating != null) {
            rating.setRating(criteriaRating.getRating());
            ratings.add(rating);
            isUpdateRating = true;
        } else {
            RatingEntity newRating = RatingEntity.builder()
                    .rating(criteriaRating.getRating())
                    .articleId(submitRatingDto.getArticleId())
                    .criteriaId(criteriaRating.getCriteriaId())
                    .userId(l) // Anonymous User
                    .guid(guid)
                    .build();
            ratings.add(newRating);
        }
        return isUpdateRating;
    }

    private void validateArticleAndCriteria(SubmitRatingDto submitRatingDto, CriteriaRatingDto criteriaRating) {
        if (!criteriaRepository.existsById(criteriaRating.getCriteriaId())) {
            throw new CriteriaDoesNotExistException("Criteria id not found", String.valueOf(criteriaRating.getCriteriaId()));
        }
        if (!articleRepository.existsById(submitRatingDto.getArticleId())) {
            throw new ArticleDoesNotExistException("Article id not found", String.valueOf(submitRatingDto.getArticleId()));
        }
    }

    public void createRandomRatings(int numberRandomRatings) throws Exception {
        Random random = new Random();
        String guid = UUID.randomUUID().toString();
        List<CriteriaEntity> criteriaList = criteriaRepository.findAll();
        for (ArticleEntity article : articleRepository.retrieveRandomArticles(numberRandomRatings)) {
            SubmitRatingDto submitRatingDto = new SubmitRatingDto();
            List<CriteriaRatingDto> criteriaRatingDtoList = new ArrayList<>();
            for (CriteriaEntity criteria : criteriaList) {
                CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
                criteriaRatingDto.setRating(random.nextInt(5) + 1);
                criteriaRatingDto.setCriteriaId(criteria.getId());
                criteriaRatingDtoList.add(criteriaRatingDto);
            }
            submitRatingDto.setRatings(criteriaRatingDtoList);
            submitRatingDto.setArticleId(article.getId());
            submitRating(submitRatingDto, guid);
        }
    }

}
