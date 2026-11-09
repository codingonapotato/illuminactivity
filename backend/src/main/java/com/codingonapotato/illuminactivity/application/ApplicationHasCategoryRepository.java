package com.codingonapotato.illuminactivity.application;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import com.codingonapotato.illuminactivity.category.Category;

public interface ApplicationHasCategoryRepository extends JpaRepository<ApplicationHasCategory, ApplicationHasCategory.PK> {
    @NativeQuery(value = """
        SELECT Category.*
        FROM ApplicationHasCategory as AC
        INNER JOIN Category ON AC.category = Category.name AND Category.deleted_at IS NULL
        WHERE AC.path = ?1 AND AC.deleted_at IS NULL
        """)
    List<Category> getActiveApplicationCategories(String path);
}