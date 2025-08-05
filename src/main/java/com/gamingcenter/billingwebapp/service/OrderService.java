package com.gamingcenter.billingwebapp.service;

import com.gamingcenter.billingwebapp.dto.OrderDTO;
import com.gamingcenter.billingwebapp.dto.OrderItemDTO;
import com.gamingcenter.billingwebapp.dto.OrderRequest;
import com.gamingcenter.billingwebapp.exceptions.ResourceNotFoundException;
import com.gamingcenter.billingwebapp.model.*;
import com.gamingcenter.billingwebapp.repository.OrderRepository;
import com.gamingcenter.billingwebapp.repository.ProductRepository;
import com.gamingcenter.billingwebapp.repository.SessionRepository;
import com.gamingcenter.billingwebapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        SessionRepository sessionRepository, UserRepository userRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.modelMapper = new ModelMapper();
    }

    @Transactional
    public OrderDTO createOrder(OrderRequest request) {
        Order order = new Order();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (request.getSessionId() != null) {
            Session session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
            order.setSession(session);
            order.setUser(session.getUser()); // Link order to session's user
        } else if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            order.setUser(user);
        } else {
            // Handle guest orders if needed, or throw error if user/session is mandatory
            throw new IllegalArgumentException("Order must be associated with a session or a user.");
        }


        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + itemRequest.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPricePerItem(product.getPrice()); // Price at the time of order

            orderItem.setOrder(order); // Set the back-reference

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // Deduct stock immediately (or upon payment, depending on business logic)
            productService.updateStock(product.getId(), -itemRequest.getQuantity());
        }
        order.setOrderItems(request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId()).get(); // Already checked for existence
                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setQuantity(itemRequest.getQuantity());
                    item.setPricePerItem(product.getPrice());
                    item.setOrder(order);
                    return item;
                }).collect(Collectors.toList()));


        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.COMPLETED); // Assuming immediate completion for snack orders

        Order savedOrder = orderRepository.save(order);
        return mapOrderToDTO(savedOrder);
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapOrderToDTO(order);
    }

    public List<OrderDTO> getOrdersBySession(Long sessionId) {
        return orderRepository.findBySessionId(sessionId).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO mapOrderToDTO(Order order) {
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
        }
        if (order.getSession() != null) {
            dto.setSessionId(order.getSession().getId());
        }
        dto.setItems(order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemDTO itemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
                    itemDTO.setProductId(orderItem.getProduct().getId());
                    itemDTO.setProductName(orderItem.getProduct().getName());
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
