package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByName(String name);
    boolean existsByName(String name);
}