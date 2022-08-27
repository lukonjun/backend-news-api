package org.comppress.customnewsapi.service.category;

import org.comppress.customnewsapi.dto.CategoryDto;
import org.comppress.customnewsapi.dto.CategoryUserDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.entity.Category;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.CategoryRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.utils.PageHolderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<GenericPage<CategoryDto>> getCategories(String lang, int page, int size) {
        Page<Category> categoryPage = categoryRepository.findByLang(lang, PageRequest.of(page, size));

        GenericPage<CategoryDto> genericPage = new GenericPage<>();
        genericPage.setData(categoryPage.stream().map(category -> mapstructMapper.categoryToCategoryDto(category)).collect(Collectors.toList()));
        BeanUtils.copyProperties(categoryPage, genericPage);

        return ResponseEntity.status(HttpStatus.OK).body(genericPage);
    }

    public ResponseEntity<GenericPage> getCategoriesUser(String lang, int page, int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if(userEntity.getListCategoryIds() == null || userEntity.getListCategoryIds().isEmpty() || doesNotContainAnyCategoriesFromLang(userEntity.getListCategoryIds(),lang)){
            Page<Category> categoryPage = categoryRepository.findByLang(lang,PageRequest.of(page, size));
            List<CategoryUserDto> categoryUserDtoList = new ArrayList<>();
            for(Category category:categoryPage.toList()){
                CategoryUserDto categoryUserDto = new CategoryUserDto();
                BeanUtils.copyProperties(category,categoryUserDto);
                categoryUserDto.setSelected(true);
                categoryUserDtoList.add(categoryUserDto);
            }
            GenericPage<CategoryUserDto> genericPage = new GenericPage<>();
            genericPage.setData(categoryUserDtoList);
            BeanUtils.copyProperties(categoryPage, genericPage);

            return ResponseEntity.status(HttpStatus.OK).body(genericPage);
        } else {
            List<Long> categoryIdList =  Stream.of(userEntity.getListCategoryIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<CategoryUserDto> categoryUserDtoList = new ArrayList<>();
            for (Category category:categoryRepository.findByLang(lang)){
                CategoryUserDto categoryUserDto = new CategoryUserDto();
                BeanUtils.copyProperties(category,categoryUserDto);
                if (categoryIdList.contains(category.getId())) {
                    categoryUserDto.setSelected(true);
                } else {
                    categoryUserDto.setSelected(false);
                }
                categoryUserDtoList.add(categoryUserDto);
            }

            return PageHolderUtils.getResponseEntityGenericPage(page, size, categoryUserDtoList);
        }
    }

    private boolean doesNotContainAnyCategoriesFromLang(String listCategoryIds, String lang) {
        List<Long> categoryIdList =  Stream.of(listCategoryIds.split(",")).map(Long::parseLong).collect(Collectors.toList());;
        for(Category category:categoryRepository.findByLang(lang)){
            if(categoryIdList.contains(category.getId())) return false;
        }
        return true;
    }
}
