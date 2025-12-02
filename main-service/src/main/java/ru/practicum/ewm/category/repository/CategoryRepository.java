package ru.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            SELECT *
            FROM categories c
            ORDER BY c.id
            LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Category> findAllWithOffset(
            @Param("from") int from,
            @Param("size") int size
    );
}
