package cz.projektant_pata.pg4.__FinalProject.repository;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
}
