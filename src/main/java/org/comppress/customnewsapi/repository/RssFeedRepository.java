package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.RssFeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RssFeedRepository extends JpaRepository<RssFeedEntity,Long> {

    Optional<RssFeedEntity> findByUrl(String url);
    Boolean existsByUrl(String url);
}
