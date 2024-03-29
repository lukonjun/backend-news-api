package org.comppress.customnewsapi.service;

import org.comppress.customnewsapi.dto.CategoryDto;
import org.comppress.customnewsapi.dto.CategoryUserDto;
import org.comppress.customnewsapi.entity.CategoryEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.CategoryRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapstructMapper mapstructMapper;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, MapstructMapper mapstructMapper, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.mapstructMapper = mapstructMapper;
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<CategoryDto>> getCategories(String lang) {
        List<CategoryEntity> categoryEntityList  = categoryRepository.findByLang(lang);

        List<CategoryDto> categoryDtoList = categoryEntityList.stream().map(mapstructMapper::categoryToCategoryDto).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(categoryDtoList);
    }

    /**
     * Retrieves the Categories of a user. You need to specify a parameter for the language. This works only for authenticated users.
     * @param lang
     * @return
     */
    public ResponseEntity<List<CategoryUserDto>> getCategoriesUser(String lang) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if(userEntity.getListCategoryIds() == null || userEntity.getListCategoryIds().isEmpty() || doesNotContainAnyCategoriesFromLang(userEntity.getListCategoryIds(),lang)){
            List<CategoryEntity> categoryEntityList = categoryRepository.findByLang(lang);
            List<CategoryUserDto> categoryUserDtoList = categoryEntityList.stream().map(category -> {
                CategoryUserDto categoryUserDto = mapstructMapper.categoryToCategoryUserDto(category);
                categoryUserDto.setSelected(true);
                return categoryUserDto;
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(categoryUserDtoList);
        } else {
            List<Long> categoryIdList = Stream.of(userEntity.getListCategoryIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<CategoryEntity> categoryEntityList = categoryRepository.findByLang(lang);
            List<CategoryUserDto> categoryUserDtoList = categoryEntityList.stream().map(category -> {
                CategoryUserDto categoryUserDto = mapstructMapper.categoryToCategoryUserDto(category);
                if(categoryIdList.contains(category.getId())){
                    categoryUserDto.setSelected(true);
                }else{
                    categoryUserDto.setSelected(false);
                }
                return categoryUserDto;
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(categoryUserDtoList);
        }
    }

    private boolean doesNotContainAnyCategoriesFromLang(String listCategoryIds, String lang) {
        List<Long> categoryIdList =  Stream.of(listCategoryIds.split(",")).map(Long::parseLong).collect(Collectors.toList());;
        for(CategoryEntity category:categoryRepository.findByLang(lang)){
            if(categoryIdList.contains(category.getId())) return false;
        }
        return true;
    }
}
