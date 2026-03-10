package cz.projektant_pata.pg4.__FinalProject.shared.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Changeable> changeables;

    @OneToMany(mappedBy = "category")
    private List<CategoryItem> categoryItems;

    @Override
    public String toString() {
        return name;  // Pro zobrazení v JComboBox
    }
}
