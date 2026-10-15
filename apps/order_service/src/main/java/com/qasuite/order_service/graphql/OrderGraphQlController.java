package com.qasuite.order_service.graphql;

import com.qasuite.order_service.model.Order;
import com.qasuite.order_service.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderGraphQlController {

    private final OrderService orderService;

    public OrderGraphQlController(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public List<Order> orders(@Argument Long userId) {
        if (userId == null) {
            return orderService.findAll();
        }
        return orderService.findByUserId(userId);
    }

    @MutationMapping
    public Order createOrder(@Argument CreateOrderInput input) {
        return orderService.create(
                input.getUserId(),
                input.getProductName(),
                input.getQuantity(),
                new BigDecimal(input.getUnitPrice()));
    }

    @MutationMapping
    public Order updateOrder(@Argument Long id, @Argument String status) {
        return orderService.updateStatus(id, status);
    }

    @MutationMapping
    public boolean deleteOrder(@Argument Long id) {
        return orderService.delete(id);
    }

    @SchemaMapping(typeName = "Order", field = "totalPrice")
    public String totalPrice(Order order) {
        return order.getTotalPrice().toPlainString();
    }

    @SchemaMapping(typeName = "Order", field = "createdAt")
    public String createdAt(Order order) {
        return order.getCreatedAt().toString();
    }
}