package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.PublisherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublisherRepository extends JpaRepository<PublisherEntity,Long> {

    Boolean existsByName(String name);
    PublisherEntity findByName(String name);
    List<PublisherEntity> findByLang(String lang);
    Page<PublisherEntity> findByLang(String lang, Pageable pageable);

}
