package org.comppress.customnewsapi.service.user;

import org.comppress.customnewsapi.dto.UserDto;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.exceptions.DuplicateEntryException;
import org.comppress.customnewsapi.exceptions.UserNotFoundException;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.utils.EncryptDecrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Value("${gdpr.secret}")
    private String key;

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder bcryptEncoder) {
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    public ResponseEntity<UserDto> saveUser(UserDto userDto) throws DuplicateEntryException {

        UserEntity entity = userDto.toEntity(UserEntity.class);
        entity.setPassword(bcryptEncoder.encode(userDto.getPassword()));
        entity.setDeleted(false);
        try {

            boolean exist = userRepository.existsByEmail(userDto.getEmail());

            if (exist){
                throw new DuplicateEntryException("Duplicate record found",String.format("%s,%s",entity.getUsername(),entity.getEmail()));
            }
            entity = userRepository.save(entity);
        }catch (DataIntegrityViolationException ex){
            throw new DuplicateEntryException("Duplicate record found",String.format("%s,%s",entity.getUsername(),entity.getEmail()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(entity.toDto(UserDto.class));
    }

    public ResponseEntity<UserDto> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        userEntity.setEmail(EncryptDecrypt.encrypt(userEntity.getEmail(), key));
        userEntity.setUsername(EncryptDecrypt.encrypt(userEntity.getUsername(), key));
        userEntity.setName(EncryptDecrypt.encrypt(userEntity.getName(), key));
        userEntity.setDeleted(true);

        userRepository.save(userEntity);

        return ResponseEntity.status(HttpStatus.OK).body(userEntity.toDto(UserDto.class));
    }

    public ResponseEntity<UserDto> getDeletedUser(String email) {

        List<UserEntity> userEntities = userRepository.findByDeletedTrue();

        for (UserEntity user : userEntities){
            String decryptedEmail = EncryptDecrypt.decrypt(user.getEmail(), key);

            if (Objects.equals(decryptedEmail, email)){

                user.setEmail(decryptedEmail);
                user.setUsername(EncryptDecrypt.decrypt(user.getUsername(), key));
                user.setName(EncryptDecrypt.decrypt(user.getName(), key));

                return ResponseEntity.status(HttpStatus.OK).body(user.toDto(UserDto.class));

            }

        }

        throw new UserNotFoundException("User not found", email);
    }
}
