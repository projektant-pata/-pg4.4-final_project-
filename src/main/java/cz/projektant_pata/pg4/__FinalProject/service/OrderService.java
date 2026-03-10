package cz.projektant_pata.pg4.__FinalProject.service;

import cz.projektant_pata.pg4.__FinalProject.event.OrderCompletedEvent;
import cz.projektant_pata.pg4.__FinalProject.event.OrderCreatedEvent;
import cz.projektant_pata.pg4.__FinalProject.event.OrderReadyEvent;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import cz.projektant_pata.pg4.__FinalProject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByStatus(String param){
        return orderRepository.findByStatus(param);
    }

    public Order save(Order order) {
        Order saved = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreatedEvent(this, saved));

        return saved;
    }

    public Order readyOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus("READY");
        Order saved = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderReadyEvent(this, saved));

        return saved;
    }

    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus("COMPLETED");
        Order saved = orderRepository.save(order);

        // Publikuj event o dokončení
        eventPublisher.publishEvent(new OrderCompletedEvent(this, saved));

        return saved;
    }


    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
