package cz.projektant_pata.pg4.__FinalProject.event;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCompletedEvent extends ApplicationEvent {
    private final Order order;

    public OrderCompletedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
}
