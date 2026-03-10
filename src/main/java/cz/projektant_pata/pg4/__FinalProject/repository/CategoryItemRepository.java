package cz.projektant_pata.pg4.__FinalProject.repository;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
}
