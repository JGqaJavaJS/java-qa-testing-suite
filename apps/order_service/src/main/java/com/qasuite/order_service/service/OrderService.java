package com.qasuite.order_service.service;

import com.qasuite.order_service.model.Order;
import com.qasuite.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order create(Long userId, String productName, Integer quantity, BigDecimal unitPrice) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return orderRepository.save(new Order(userId, productName, quantity, total));
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}