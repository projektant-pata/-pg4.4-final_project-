package cz.projektant_pata.pg4.__FinalProject.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartDTO {
    private List<CartItemDTO> items = new ArrayList<>();

    public void addItem(CartItemDTO item) {
        items.add(item);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clear() {
        items.clear();
    }
}
