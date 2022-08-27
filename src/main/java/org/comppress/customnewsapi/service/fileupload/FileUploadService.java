package org.comppress.customnewsapi.service.fileupload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.comppress.customnewsapi.dto.CategoryDto;
import org.comppress.customnewsapi.dto.CriteriaDto;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.dto.TopNewsFeedDto;
import org.comppress.customnewsapi.entity.*;
import org.comppress.customnewsapi.exceptions.FileImportException;
import org.comppress.customnewsapi.exceptions.PublisherDoesNotExistException;
import org.comppress.customnewsapi.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileUploadService {

    private final int TOP_NEWS_PUBLISHER = 0;
    private final int TOP_NEWS_LINK = 1;
    private final int TOP_NEWS_LANG = 2;

    private final int RSS_FEED_SOURCE = 0;
    private final int RSS_FEED_CATEGORY = 1;
    private final int RSS_FEED_LINK = 2;
    private final int RSS_FEED_LANG = 3;

    private final int CRITERIA_ID = 0;
    private final int CRITERIA_NAME = 1;

    private final int PUBLISHER_NAME = 0;
    private final int PUBLISHER_SVG_URL = 1;

    private final int CATEGORY_LANG = 0;
    private final int CATEGORY_NAME = 1;
    private final int CATEGORY_IMG_URL = 2;

    private final RssFeedRepository rssFeedRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final CriteriaRepository criteriaRepository;
    private final TopNewsFeedRepository topNewsFeedRepository;

    @Autowired
    public FileUploadService(RssFeedRepository rssFeedRepository, PublisherRepository publisherRepository, CategoryRepository categoryRepository, CriteriaRepository criteriaRepository, TopNewsFeedRepository topNewsFeedRepository) {
        this.rssFeedRepository = rssFeedRepository;
        this.publisherRepository = publisherRepository;
        this.categoryRepository = categoryRepository;
        this.criteriaRepository = criteriaRepository;
        this.topNewsFeedRepository = topNewsFeedRepository;
    }

    @Transactional
    public ResponseEntity<List<RssFeed>> saveRssFeeds(MultipartFile file) throws Exception {

        log.info("LINKS IMPORT CSV IS PROCESSING {}", file.getName());

        List<CSVRecord> csvRecordList = getRecords(file);
        List<RssFeed> rssFeedList = getRssFeeds(csvRecordList);
        List<RssFeed> finalRssFeedList = new ArrayList<>();
        // TODO Also update Feeds
        for (RssFeed rssFeed : rssFeedList) {
            if (rssFeedRepository.findByUrl(rssFeed.getUrl()).isEmpty()) finalRssFeedList.add(rssFeed);
        }
        try {
            rssFeedRepository.saveAll(finalRssFeedList);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Logging
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(finalRssFeedList);
    }

    private List<CSVRecord> getRecords(MultipartFile file) {
        try {
            CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8), CSVFormat.DEFAULT.withDelimiter(';'));
            return csvParser.getRecords();
        } catch (IOException e) {
            throw new FileImportException("Failed to import csv file", file.getName());
        }
    }

    private List<RssFeed> getRssFeeds(List<CSVRecord> csvRecordList) throws Exception {
        List<RssFeed> rssFeedList = new ArrayList<>();

        for (CSVRecord record : csvRecordList) {
            Publisher publisher = publisherRepository.findByName(record.get(RSS_FEED_SOURCE));
            if (publisher == null) {
                publisher = new Publisher(record.get(RSS_FEED_SOURCE),record.get(RSS_FEED_LANG),"");
                publisherRepository.save(publisher);
            }
            Category category = categoryRepository.findByNameAndLang(record.get(RSS_FEED_CATEGORY),record.get(RSS_FEED_LANG));
            if (category == null) {
                throw new RuntimeException("Category should not be null during import "+record.get(RSS_FEED_CATEGORY) + " : " + record.get(RSS_FEED_LANG)+
                        "call /category before"
                );
                /*
                category = new Category(record.get(RSS_FEED_CATEGORY), record.get(RSS_FEED_LANG),"");
                categoryRepository.save(category);
                 */
            }
            rssFeedList.add(RssFeed.builder()
                    .publisherId(publisher.getId())
                    .categoryId(category.getId())
                    .lang(record.get(RSS_FEED_LANG))
                    .url(record.get(RSS_FEED_LINK)).build());
        }

        return rssFeedList;
    }

    public ResponseEntity<List<CriteriaDto>> saveCriteria(MultipartFile file) {
        log.info("LINKS IMPORT CSV IS PROCESSING {}", file.getName());

        List<CSVRecord> csvRecordList = getRecords(file);
        List<CriteriaDto> criteriaDtoList = new ArrayList<>();
        for(CSVRecord csvRecord:csvRecordList){
            if(criteriaRepository.existsById(Long.parseLong(csvRecord.get(CRITERIA_ID)))){
                continue;
            }
            Criteria criteria = new Criteria();
            criteria.setId(Long.parseLong(csvRecord.get(CRITERIA_ID)));
            criteria.setName(csvRecord.get(CRITERIA_NAME));
            criteriaRepository.save(criteria);
            CriteriaDto criteriaDto = new CriteriaDto();
            BeanUtils.copyProperties(criteria,criteriaDto);
            criteriaDtoList.add(criteriaDto);
        }
        return ResponseEntity.ok().body(criteriaDtoList);
    }

    public ResponseEntity<List<PublisherDto>> savePublisherSVGs(MultipartFile file) {
        log.info("LINKS IMPORT CSV IS PROCESSING {}", file.getName());

        List<CSVRecord> csvRecordList = getRecords(file);
        List<PublisherDto> publisherDtoList = new ArrayList<>();
        for(CSVRecord csvRecord:csvRecordList){
            Publisher publisher = publisherRepository.findByName(csvRecord.get(PUBLISHER_NAME));
            if(publisher != null){
                publisher.setUrlToImage(csvRecord.get(PUBLISHER_SVG_URL));
                publisherRepository.save(publisher);
                PublisherDto publisherDto = new PublisherDto();
                BeanUtils.copyProperties(publisher, publisherDto);
                publisherDtoList.add(publisherDto);
            }
        }
        return ResponseEntity.ok().body(publisherDtoList);
    }

    public ResponseEntity<List<TopNewsFeedDto>> saveTopNews(MultipartFile file) {
        log.info("LINKS IMPORT CSV IS PROCESSING {}", file.getName());

        List<CSVRecord> csvRecordList = getRecords(file);
        List<TopNewsFeedDto> topNewsFeedDtos = new ArrayList<>();
        for(CSVRecord csvRecord:csvRecordList){
            if(!topNewsFeedRepository.existsByUrl(csvRecord.get(TOP_NEWS_LINK))){
                TopNewsFeed topNewsFeed = new TopNewsFeed();
                topNewsFeed.setLang(csvRecord.get(TOP_NEWS_LANG));
                topNewsFeed.setUrl(csvRecord.get(TOP_NEWS_LINK));
                Publisher publisher = publisherRepository.findByName(csvRecord.get(TOP_NEWS_PUBLISHER));
                if(publisher != null){
                    topNewsFeed.setPublisherId(publisher.getId());
                }else{
                    throw new PublisherDoesNotExistException("Publisher does not exist", csvRecord.get(TOP_NEWS_PUBLISHER));
                }
                topNewsFeedRepository.save(topNewsFeed);
                TopNewsFeedDto topNewsFeedDto = new TopNewsFeedDto();
                BeanUtils.copyProperties(topNewsFeed,topNewsFeedDto);
                topNewsFeedDtos.add(topNewsFeedDto);
            }
        }
        return ResponseEntity.ok().body(topNewsFeedDtos);
    }

    /**
     * Special upload method as it takes care of name, language and img_url
     * opposing to publisher where there is an extra upload method to upload the image urls
     * @param file
     * @return
     */
    public ResponseEntity<List<CategoryDto>> saveCategories(MultipartFile file) {
        log.info("LINKS IMPORT CSV IS PROCESSING {}", file.getName());

        List<CSVRecord> csvRecordList = getRecords(file);
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for(CSVRecord csvRecord:csvRecordList){
            if(categoryRepository.findByNameAndLang(csvRecord.get(CATEGORY_NAME),csvRecord.get(CATEGORY_LANG)) == null){
                Category category = new Category();
                category.setName(csvRecord.get(CATEGORY_NAME));
                category.setLang(csvRecord.get(CATEGORY_LANG));
                category.setUrlToImage(csvRecord.get(CATEGORY_IMG_URL));
                categoryRepository.save(category);
                CategoryDto categoryDto = new CategoryDto();
                BeanUtils.copyProperties(category,categoryDto);
                categoryDtoList.add(categoryDto);
            }
        }
        return ResponseEntity.ok().body(categoryDtoList);
    }
}
