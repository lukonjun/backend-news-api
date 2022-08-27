package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.CategoryDto;
import org.comppress.customnewsapi.dto.CriteriaDto;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.dto.TopNewsFeedDto;
import org.comppress.customnewsapi.entity.RssFeed;
import org.comppress.customnewsapi.service.fileupload.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value ="/links", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<RssFeed>> saveRssFeeds(@RequestParam("file") MultipartFile file) throws Exception {
        return fileUploadService.saveRssFeeds(file);
    }

    @PostMapping(value ="/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CategoryDto>> saveCategories(@RequestParam("file") MultipartFile file){
        return fileUploadService.saveCategories(file);
    }

    @PostMapping(value ="/publisher-svg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PublisherDto>> savePublisherSVGs(@RequestParam("file") MultipartFile file){
        return fileUploadService.savePublisherSVGs(file);
    }

    @PostMapping(value ="/criteria", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CriteriaDto>> saveCriteria(@RequestParam("file") MultipartFile file){
        return fileUploadService.saveCriteria(file);
    }

    @PostMapping(value ="/top-news", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<TopNewsFeedDto>> saveTopNews(@RequestParam("file") MultipartFile file){
        return fileUploadService.saveTopNews(file);
    }

}
