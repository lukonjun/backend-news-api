package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.*;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.exceptions.EmailAlreadyExistsException;
import org.comppress.customnewsapi.exceptions.EmailSenderException;
import org.comppress.customnewsapi.service.article.ArticleService;
import org.comppress.customnewsapi.service.profile.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ArticleService articleService;

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<ResponseDto> sendOtp(@RequestBody @Valid ForgetPasswordDto forgetPasswordDto) throws EmailSenderException, EmailAlreadyExistsException {
        return profileService.sendOtp(forgetPasswordDto);
    }

    @PostMapping(value = "/update-password")
    public ResponseEntity<UpdatePasswordDto> updatePassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto) throws EmailSenderException, EmailAlreadyExistsException {
        return profileService.updatePassword(updatePasswordDto);
    }

    @PostMapping(value = "/preferences")
    public ResponseEntity<UserDto> updateCategoryAndPublisherPreference(@RequestBody PreferenceDto preferenceDto) {
        return profileService.updateCategoryAndPublisherPreference(preferenceDto);
    }

    @PostMapping(value = "/preferences/reset")
    public ResponseEntity<UserDto> resetPreferences() {
        return profileService.resetPreferences();
    }

    @GetMapping(value = "/personal")
    ResponseEntity<GenericPage> getRatedArticles(@RequestParam(value = "page") int page,
                                                 @RequestParam(value = "size") int size,
                                                 @RequestParam(value = "fromDate", required = false) String fromDate,
                                                 @RequestParam(value = "toDate", required = false) String toDate
    ) {
        return articleService.getPersonalRatedArticlesFromUser(page, size, fromDate, toDate);
    }

}

