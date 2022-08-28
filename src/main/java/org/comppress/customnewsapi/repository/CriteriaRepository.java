package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.CriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriteriaRepository extends JpaRepository<CriteriaEntity, Long> {
    boolean existsById(Long id);
}
