package org.comppress.customnewsapi.loader;

import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.repository.CategoryRepository;
import org.comppress.customnewsapi.repository.RssFeedRepository;
import org.comppress.customnewsapi.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EntityLoader implements ApplicationRunner {

    private final RssFeedRepository rssFeedRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public EntityLoader(RssFeedRepository rssFeedRepository, PublisherRepository publisherRepository, CategoryRepository categoryRepository) {
        this.rssFeedRepository = rssFeedRepository;
        this.publisherRepository = publisherRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        boolean tmp = true;
        if(tmp) return;
        // https://stackoverflow.com/questions/44399422/read-file-from-resources-folder-in-spring-boot
        /*
        File file = null;
        try {
            file = new ClassPathResource("json/news-feeds.json").getFile();
        }catch (IOException e){
            log.error("Cant read RSS feeds from json");
            e.printStackTrace();
            return;
        }
        RootDto jsonModel = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonModel = mapper.readValue(file, RootDto.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int counterPublisher = 0;
        int counterRssFeed = 0;
        int counterCategory = 0;
        for (PublisherDto publisherDto : jsonModel.getPublisherDtos()) {
            // Create Publisher
            if (!publisherRepository.existsByName(publisherDto.getPublisher())) {
                publisherRepository.save(new Publisher(publisherDto.getPublisher()));
                counterPublisher++;
            }
            // Create Category
            if(!categoryRepository.existsByName(publisherDto.getCategory())){
                categoryRepository.save(new Category(publisherDto.getCategory()));
                counterCategory++;
            }

            // Create RssFeed
            if (!rssFeedRepository.existsByUrl(publisherDto.getRSSFeed())) {
                RssFeed rssFeed = RssFeed.builder()
                        .url(publisherDto.getRSSFeed())
                        .categoryId(categoryRepository.findByName(publisherDto.getCategory()).getId())
                        .publisherId(publisherRepository.findByName(publisherDto.getPublisher()).getId())
                        .build();
                rssFeedRepository.save(rssFeed);
                counterRssFeed++;
            }
        }
        log.info("Created " + counterPublisher + " Publisher, " + counterCategory + " Categories and " + counterRssFeed + " Rss Feed");
        log.info("Exiting Entity Loader now");
           */
    }
}
