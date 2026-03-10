package cz.projektant_pata.pg4.__FinalProject;

import cz.projektant_pata.pg4.__FinalProject.repository.CategoryRepository;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/test")
    public String test() {
        long count = categoryRepository.count();
        return "Spring Boot + MySQL funguje! Počet kategorií v DB: " + count;
    }

    @GetMapping("/test/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
