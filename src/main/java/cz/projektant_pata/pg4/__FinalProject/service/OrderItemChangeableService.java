package cz.projektant_pata.pg4.__FinalProject.service;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.OrderItemChangeable;
import cz.projektant_pata.pg4.__FinalProject.repository.OrderItemChangeableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemChangeableService {

    @Autowired
    private OrderItemChangeableRepository orderItemChangeableRepository;

    public List<OrderItemChangeable> findAll() {
        return orderItemChangeableRepository.findAll();
    }

    public Optional<OrderItemChangeable> findById(Long id) {
        return orderItemChangeableRepository.findById(id);
    }

    public OrderItemChangeable save(OrderItemChangeable orderItemChangeable) {
        return orderItemChangeableRepository.save(orderItemChangeable);
    }

    public void deleteById(Long id) {
        orderItemChangeableRepository.deleteById(id);
    }
}
