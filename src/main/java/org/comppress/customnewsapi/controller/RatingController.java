package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.SubmitRatingDto;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;


    @PostMapping("/submit")
    public ResponseEntity<ResponseDto> submitRating(@RequestBody SubmitRatingDto submitRatingDto, @RequestParam(required = false) String guid) throws Exception {
        return ratingService.submitRating(submitRatingDto,guid);
    }

    @GetMapping("/generate")
    public ResponseEntity<String> createRandomRatings(@RequestParam(defaultValue = "100")int numberRandomRatings) throws Exception {
        ratingService.createRandomRatings(100);
        return ResponseEntity.ok().body("Successfully generated " + numberRandomRatings + " random ratings");
    }
}
