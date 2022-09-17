package org.comppress.customnewsapi.service.publisher;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.dto.PublisherUserDto;
import org.comppress.customnewsapi.entity.PublisherEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.PublisherRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final UserRepository userRepository;
    private final MapstructMapper mapstructMapper;

    public ResponseEntity<List<PublisherDto>> getPublisher(String lang) {
        List<PublisherEntity> publisherEntityList = publisherRepository.findByLang(lang);

        List<PublisherDto> publisherDtoList = publisherEntityList.stream().map(mapstructMapper::publisherToPublisherDto).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(publisherDtoList);
    }

    public ResponseEntity<List<PublisherUserDto>> getPublisherUser(String lang) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if (userEntity.getListCategoryIds() == null || userEntity.getListCategoryIds().isEmpty() || doesNotContainAnyPublishersFromLang(userEntity.getListPublisherIds(), lang)) {
            List<PublisherEntity> publisherEntityList = publisherRepository.findByLang(lang);
            List<PublisherUserDto> publisherUserDtoList = publisherEntityList.stream().map( publisher -> {
                PublisherUserDto publisherUserDto = mapstructMapper.publisherToPublisherUserDto(publisher);
                publisherUserDto.setSelected(true);
                return publisherUserDto;
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(publisherUserDtoList);
        } else {
            List<Long> publisherIdList = Stream.of(userEntity.getListPublisherIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<PublisherEntity> publisherEntityList =  publisherRepository.findByLang(lang);
            List<PublisherUserDto> publisherUserDtoList =  publisherEntityList.stream().map(publisher -> {
                PublisherUserDto publisherUserDto = mapstructMapper.publisherToPublisherUserDto(publisher);
                if(publisherIdList.contains(publisher.getId())){
                    publisherUserDto.setSelected(true);
                } else {
                    publisherUserDto.setSelected(false);
                }
                return publisherUserDto;
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(publisherUserDtoList);
        }
    }

    private boolean doesNotContainAnyPublishersFromLang(String listPublisherIds, String lang) {
        List<Long> publisherIdList = Stream.of(listPublisherIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        for (PublisherEntity publisher : publisherRepository.findByLang(lang)) {
            if (publisherIdList.contains(publisher.getId())) return false;
        }
        return true;
    }

}
