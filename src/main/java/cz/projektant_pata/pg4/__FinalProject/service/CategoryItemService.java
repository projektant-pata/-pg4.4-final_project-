package cz.projektant_pata.pg4.__FinalProject.service;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.CategoryItem;
import cz.projektant_pata.pg4.__FinalProject.repository.CategoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryItemService {

    @Autowired
    private CategoryItemRepository categoryItemRepository;

    public List<CategoryItem> findAll() {
        return categoryItemRepository.findAll();
    }

    public Optional<CategoryItem> findById(Long id) {
        return categoryItemRepository.findById(id);
    }

    public CategoryItem save(CategoryItem item) {
        return categoryItemRepository.save(item);
    }

    public void deleteById(Long id) {
        categoryItemRepository.deleteById(id);
    }
}
