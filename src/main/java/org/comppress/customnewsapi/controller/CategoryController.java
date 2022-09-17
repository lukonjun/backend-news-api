package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.CategoryDto;
import org.comppress.customnewsapi.dto.CategoryUserDto;
import org.comppress.customnewsapi.service.category.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang
    ){
        return categoryService.getCategories(lang);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CategoryUserDto>> getCategoriesUser(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang
    ){
        return categoryService.getCategoriesUser(lang);
    }

}
