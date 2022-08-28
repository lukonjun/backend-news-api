package org.comppress.customnewsapi.utils;

import org.comppress.customnewsapi.dto.article.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public interface GenerateGenericPageUtils {

    default GenericPage<CustomRatedArticleDto> getCustomRatedArticleDtoGenericPage(Page<CustomRatedArticle> customRatedArticlePage) {
        GenericPage<CustomRatedArticleDto> genericPage = new GenericPage<>();
        List<CustomRatedArticleDto> customRatedArticleDtoList = new ArrayList<>();
        for(CustomRatedArticle customRatedArticle: customRatedArticlePage.toList()){
            CustomRatedArticleDto customRatedArticleDto = new CustomRatedArticleDto();
            BeanUtils.copyProperties(customRatedArticle,customRatedArticleDto);
            customRatedArticleDtoList.add(customRatedArticleDto);
        }
        genericPage.setData(customRatedArticleDtoList);
        BeanUtils.copyProperties(customRatedArticlePage,genericPage);
        return genericPage;
    }

}
