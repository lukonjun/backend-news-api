package org.comppress.customnewsapi.service.publisher;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.dto.PublisherUserDto;
import org.comppress.customnewsapi.entity.PublisherEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.PublisherRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.utils.PageHolderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final UserRepository userRepository;
    private final MapstructMapper mapstructMapper;


    public ResponseEntity<GenericPage<PublisherDto>> getPublisher(String lang, int page, int size) {
        Page<PublisherEntity> publisherPage = publisherRepository.findByLang(lang, PageRequest.of(page, size));

        GenericPage<PublisherDto> genericPage = new GenericPage<>();
        genericPage.setData(publisherPage.stream().map(publisher -> mapstructMapper.publisherToPublisherDto(publisher)).collect(Collectors.toList()));
        BeanUtils.copyProperties(publisherPage, genericPage);
        return ResponseEntity.status(HttpStatus.OK).body(genericPage);
    }

    public ResponseEntity<GenericPage> getPublisherUser(String lang, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if (userEntity.getListCategoryIds() == null || userEntity.getListCategoryIds().isEmpty() || doesNotContainAnyPublishersFromLang(userEntity.getListPublisherIds(), lang)) {
            Page<PublisherEntity> publisherPage = publisherRepository.findByLang(lang, PageRequest.of(page, size));
            List<PublisherUserDto> publisherUserDtoList = new ArrayList<>();
            for (PublisherEntity publisher : publisherPage.toList()) {
                PublisherUserDto publisherUserDto = new PublisherUserDto();
                BeanUtils.copyProperties(publisher, publisherUserDto);
                publisherUserDto.setSelected(true);
                publisherUserDtoList.add(publisherUserDto);
            }
            GenericPage<PublisherUserDto> genericPage = new GenericPage<>();
            genericPage.setData(publisherUserDtoList);
            BeanUtils.copyProperties(publisherPage, genericPage);

            return ResponseEntity.status(HttpStatus.OK).body(genericPage);
        } else {
            List<Long> publisherIdList = Stream.of(userEntity.getListPublisherIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<PublisherUserDto> publisherUserDtoList = new ArrayList<>();
            for (PublisherEntity publisher : publisherRepository.findByLang(lang)) {
                PublisherUserDto publisherUserDto = new PublisherUserDto();
                BeanUtils.copyProperties(publisher, publisherUserDto);
                if (publisherIdList.contains(publisher.getId())) {
                    publisherUserDto.setSelected(true);
                } else {
                    publisherUserDto.setSelected(false);
                }
                publisherUserDtoList.add(publisherUserDto);
            }

            return PageHolderUtils.getResponseEntityGenericPage(page, size, publisherUserDtoList);
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
