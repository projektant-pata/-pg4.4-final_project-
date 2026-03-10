package cz.projektant_pata.pg4.__FinalProject.dto;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.CategoryItem;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.Changeable;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartItemDTO {
    private CategoryItem product;
    private int count;
    private List<Changeable> selectedChangeables = new ArrayList<>();

    public BigDecimal getTotalPrice() {
        BigDecimal basePrice = product.getPrice().multiply(BigDecimal.valueOf(count));
        BigDecimal changeablesPrice = selectedChangeables.stream()
                .map(Changeable::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(count));
        return basePrice.add(changeablesPrice);
    }
}
