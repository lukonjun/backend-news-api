package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.ForgetPasswordDto;
import org.comppress.customnewsapi.dto.PreferenceDto;
import org.comppress.customnewsapi.dto.UpdatePasswordDto;
import org.comppress.customnewsapi.dto.UserDto;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.exceptions.EmailAlreadyExistsException;
import org.comppress.customnewsapi.exceptions.EmailSenderException;
import org.comppress.customnewsapi.service.profile.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

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

}

