package org.comppress.customnewsapi.service;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.ForgetPasswordDto;
import org.comppress.customnewsapi.dto.PreferenceDto;
import org.comppress.customnewsapi.dto.UpdatePasswordDto;
import org.comppress.customnewsapi.dto.UserDto;
import org.comppress.customnewsapi.dto.response.ForgetPasswordResponseDto;
import org.comppress.customnewsapi.dto.response.ResponseDto;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.exceptions.EmailAlreadyExistsException;
import org.comppress.customnewsapi.exceptions.EmailSenderException;
import org.comppress.customnewsapi.exceptions.PasswordNotMatchException;
import org.comppress.customnewsapi.exceptions.UserNotFoundException;
import org.comppress.customnewsapi.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder bcryptEncoder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ResponseEntity<ResponseDto> sendOtp(ForgetPasswordDto forgetPasswordDto) throws EmailSenderException, EmailAlreadyExistsException {
        UserEntity user = userRepository.findByEmailAndDeletedFalse(forgetPasswordDto.getEmail()).
                orElseThrow(() -> new UserNotFoundException("User not found",forgetPasswordDto.getEmail()));

        String otp = String.format("%06d", new Random().nextInt(999999));

        user.setOtp(otp);
        user.setOtpUsed(false);
        userRepository.save(user);

        Properties properties = new Properties();

        properties.setProperty("to", forgetPasswordDto.getEmail());
        properties.setProperty("from", fromEmail);
        properties.setProperty("subject", "Forget password OTP");
        properties.setProperty("template_name", "email_template");
        properties.setProperty("otp", otp);
        properties.setProperty("date", new Date() + "");

        emailService.sendAutomatedEmailWithTemplate(properties);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ForgetPasswordResponseDto.builder()
                        .message("Please check your email")
                        .build());
    }

    public ResponseEntity<UpdatePasswordDto> updatePassword(UpdatePasswordDto updatePasswordDto) throws EmailSenderException, EmailAlreadyExistsException {
        UserEntity user = userRepository.findByOtpAndEmailAndIsOtpUsedFalse(updatePasswordDto.getOtp(),updatePasswordDto.getEmail()).
                orElseThrow(() -> new UserNotFoundException("User not found for given otp and email",updatePasswordDto.getEmail()));

        if (!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new PasswordNotMatchException("New Passwords are Not Matching", "Please check");
        }

        user.setPassword(encryptPassword(updatePasswordDto.getNewPassword()));
        user.setOtpUsed(true);
        userRepository.save(user);

        Properties properties = new Properties();

        properties.setProperty("to", updatePasswordDto.getEmail());
        properties.setProperty("from", fromEmail);
        properties.setProperty("subject", "Password has been updated successfully");
        properties.setProperty("template_name", "update_password_template");

        emailService.sendAutomatedEmailWithTemplate(properties);

        return ResponseEntity.ok(updatePasswordDto);
    }


    public ResponseEntity<UserDto> updateCategoryAndPublisherPreference(PreferenceDto preferenceDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());
        if(userEntity == null){ return ResponseEntity.status(401).body(null);}
        userEntity.setListCategoryIds(preferenceDto.getListOfCategoryIds());
        userEntity.setListPublisherIds(preferenceDto.getListOfPublisherIds());
        userRepository.save(userEntity);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return ResponseEntity.ok().body(userDto);
    }

    public ResponseEntity<UserDto> resetPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());
        if(userEntity == null){ return ResponseEntity.status(401).body(null);}
        userEntity.setListCategoryIds(null);
        userEntity.setListPublisherIds(null);
        userRepository.save(userEntity);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return ResponseEntity.ok().body(userDto);
    }

    private String encryptPassword(String password) {

        return bcryptEncoder.encode(password);
    }
}
