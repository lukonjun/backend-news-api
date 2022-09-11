package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long>, JpaSpecificationExecutor<CategoryEntity> {
    List<CategoryEntity> findByName(String name);
    CategoryEntity findByNameAndLang(String name, String lang);
    Page<CategoryEntity> findByLang(String lang, Pageable pageable);
    List<CategoryEntity> findByLang(String lang);
    @Query(value = "SELECT * FROM category c WHERE c.id IN (:ids) ORDER BY FIELD(id, :ids)", nativeQuery = true)     // 2. Spring JPA In cause using @Query
    List<CategoryEntity> findByCategoryIds(@Param("ids")List<Long> ids);
}