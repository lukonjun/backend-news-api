package org.comppress.customnewsapi.service.rating;

import lombok.SneakyThrows;
import org.comppress.customnewsapi.dto.CriteriaRatingDto;
import org.comppress.customnewsapi.dto.SubmitRatingDto;
import org.comppress.customnewsapi.dto.response.CreateRatingResponseDto;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.dto.response.UpdateRatingResponseDto;
import org.comppress.customnewsapi.entity.RatingEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.exceptions.ArticleDoesNotExistException;
import org.comppress.customnewsapi.exceptions.CriteriaDoesNotExistException;
import org.comppress.customnewsapi.exceptions.RatingIsInvalidException;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.CriteriaRepository;
import org.comppress.customnewsapi.repository.RatingRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.security.AppAuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RatingService.class})
class RatingServiceImplTests {

    @Autowired
    private RatingService ratingService;

    @MockBean
    private RatingRepository ratingRepositoryMock;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private CriteriaRepository criteriaRepositoryMock;

    @MockBean
    private ArticleRepository articleRepositoryMock;

    @MockBean
    private AppAuthenticationService appAuthenticationServiceMock;

    @Test
    @SneakyThrows
    @DisplayName("Given a valid rating id by an authenticated user then save to database")
    public void submitRating_givenValidRatingAuthenticatedUserExistingArticle_saveToDb(){
        when(appAuthenticationServiceMock.getCurrentUsername()).thenReturn("lucas");

        UserEntity userEntity = new UserEntity();
        userEntity.setName("lucas");
        userEntity.setEmail("max@mustermann.de");
        userEntity.setUsername("lucas1234");
        userEntity.setId(3L);

        when(userRepositoryMock.findByUsernameAndDeletedFalse("lucas")).thenReturn(userEntity);

        when(articleRepositoryMock.existsById(1234L)).thenReturn(true);

        when(criteriaRepositoryMock.existsById(1L)).thenReturn(true);

        RatingEntity rating = new RatingEntity(1L, 3L, 1234L, 2, "-");

        when(ratingRepositoryMock.findByUserIdAndArticleIdAndCriteriaId(userEntity.getId(), 1234L, 1L))
                .thenReturn(rating);

        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(2);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = null;

        UpdateRatingResponseDto expectedInternal = UpdateRatingResponseDto.builder()
                .message("Updated rating for article")
                .submitRatingDto(submitRatingDto)
                .build();

        ResponseEntity<ResponseDto> expected = ResponseEntity.status(HttpStatus.CREATED).body(expectedInternal);

        ResponseEntity<ResponseDto> actual = ratingService.submitRating(submitRatingDto, guid);

        assertEquals(expected,actual);

        Mockito.verify(ratingRepositoryMock, Mockito.times(1)).saveAll(List.of(rating));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given an invalid rating id by an authenticated user then save to database")
    public void submitRating_givenInValidRatingAuthenticatedUserExistingArticle_throwException(){
        when(appAuthenticationServiceMock.getCurrentUsername()).thenReturn("lucas");

        UserEntity userEntity = new UserEntity();
        userEntity.setName("lucas");
        userEntity.setEmail("max@mustermann.de");
        userEntity.setUsername("lucas1234");
        userEntity.setId(3L);

        when(userRepositoryMock.findByUsernameAndDeletedFalse("lucas")).thenReturn(userEntity);

        when(articleRepositoryMock.existsById(1234L)).thenReturn(true);

        when(criteriaRepositoryMock.existsById(1L)).thenReturn(true);

        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(-1);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = null;

        RatingIsInvalidException ratingIsInvalidException = assertThrows(RatingIsInvalidException.class, () -> {
            ratingService.submitRating(submitRatingDto, guid);
        });

        assertEquals("invalid rating: -1 (valid values are 1 - 5)", ratingIsInvalidException.getMessage());
        assertEquals("-1", ratingIsInvalidException.getVariable());

    }

    @Test
    @SneakyThrows
    public void submitRating_givenValidRatingWithGuidExistingArticle_saveToDb(){
        when(articleRepositoryMock.existsById(1234L)).thenReturn(true);

        when(criteriaRepositoryMock.existsById(1L)).thenReturn(true);

        RatingEntity rating = new RatingEntity(1L, 3L, 1234L, 2, "fooBar");

        when(ratingRepositoryMock.findByGuidAndArticleIdAndCriteriaId("fooBar", 1234L, 1L))
                .thenReturn(rating);

        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(2);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        UpdateRatingResponseDto expectedInternal = UpdateRatingResponseDto.builder()
                .message("Updated rating for article")
                .submitRatingDto(submitRatingDto)
                .build();

        ResponseEntity<ResponseDto> expected = ResponseEntity.status(HttpStatus.CREATED).body(expectedInternal);

        ResponseEntity<ResponseDto> actual = ratingService.submitRating(submitRatingDto, guid);

        assertEquals(expected,actual);

        Mockito.verify(ratingRepositoryMock, Mockito.times(1)).saveAll(List.of(rating));
    }

    @Test
    @SneakyThrows
    public void submitRating_givenValidRatingWithGuidNewArticle_saveToDb(){
        when(articleRepositoryMock.existsById(1234L)).thenReturn(true);

        when(criteriaRepositoryMock.existsById(1L)).thenReturn(true);

        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(2);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        CreateRatingResponseDto expectedInternal = CreateRatingResponseDto.builder()
                .message("Created rating for article")
                .submitRatingDto(submitRatingDto)
                .build();

        ResponseEntity<ResponseDto> expected = ResponseEntity.status(HttpStatus.CREATED).body(expectedInternal);

        ResponseEntity<ResponseDto> actual = ratingService.submitRating(submitRatingDto, guid);

        assertEquals(expected,actual);

        Mockito.verify(ratingRepositoryMock, Mockito.times(1)).saveAll(Mockito.any());
    }

    @Test
    public void submitRating_givenWrongCriteriaId_throwException(){
        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(5);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        CriteriaDoesNotExistException actualException = assertThrows(CriteriaDoesNotExistException.class, () -> {
            ratingService.submitRating(submitRatingDto, guid);
        });

        assertEquals("Criteria id not found", actualException.getMessage());
        assertEquals("1", actualException.getVariable());

        Mockito.verify(criteriaRepositoryMock, Mockito.times(1)).existsById(1L);
    }

    @Test
    public void submitRating_givenWrongArticleId_throwException(){
        when(criteriaRepositoryMock.existsById(1L)).thenReturn(true);

        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(5);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        ArticleDoesNotExistException articleDoesNotExistException = assertThrows(ArticleDoesNotExistException.class, () -> {
            ratingService.submitRating(submitRatingDto, guid);
        });

//        Executable executable = new Executable() {
//            @Override
//            public void execute() throws Throwable {
//                ratingService.submitRating(submitRatingDto, guid);
//            }
//        };
//        assertThrows(ArticleDoesNotExistException.class, executable);

        assertEquals("Article id not found", articleDoesNotExistException.getMessage());
        assertEquals("1234", articleDoesNotExistException.getVariable());

        Mockito.verify(articleRepositoryMock, Mockito.times(1)).existsById(1234L);
    }

    @Test
    public void submitRating_givenInvalidRatingUpperBound_throwException(){
        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(6);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        assertThrows(RatingIsInvalidException.class, () -> {
            ratingService.submitRating(submitRatingDto, guid);
        });
    }

    @Test
    public void submitRating_givenInvalidRatingLowerBound_throwException(){
        SubmitRatingDto submitRatingDto = new SubmitRatingDto();
        submitRatingDto.setArticleId(1234L);
        CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
        criteriaRatingDto.setCriteriaId(1L);
        criteriaRatingDto.setRating(0);
        submitRatingDto.setRatings(List.of(criteriaRatingDto));

        String guid = "fooBar";

        RatingIsInvalidException ratingIsInvalidException = assertThrows(RatingIsInvalidException.class, () -> {
            ratingService.submitRating(submitRatingDto, guid);
        });

        assertEquals("invalid rating: 0 (valid values are 1 - 5)", ratingIsInvalidException.getMessage());
        assertEquals("0", ratingIsInvalidException.getVariable());
    }

}