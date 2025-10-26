package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.entity.Order;
import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;
import com.example.quanlicuahangthuoc.repository.OrderRepository;
import com.example.quanlicuahangthuoc.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    public Order getOrderById(Integer id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy đơn hàng với ID: " + id));
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public Page<Order> getOrderPage(Integer customerId, LocalDate fromDate, LocalDate toDate, Order.OrderStatus status, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
            direction = Sort.Direction.DESC;
        }

        Sort sort;
        if ("orderDate".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "orderDate");
        else if ("totalAmount".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "totalAmount");
        else if ("customer.id".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "customer.id");
        else if ("staff.id".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "staff.id");
        else if ("status".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "status");
        else if ("promotion.id".equalsIgnoreCase(sortBy)) sort = Sort.by(direction, "promotion.id");
        else sort = Sort.by(direction, "id");

        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.findOrdersByDateRange(customerId, fromDate, toDate, status, pageable);
    }

    public long getTotalOrders() {
        return orderRepository.countOrders();
    }

    @Transactional
    public Order addOrder(Order order) {
        return processOrder(order);
    }

    @Transactional
    public Order updateOrder(Order order) {
        if (!orderRepository.existsById(order.getId())) {
            throw new NoSuchElementException("Không tìm thấy đơn hàng với ID: " + order.getId());
        }
        return processOrder(order);
    }

    private Order processOrder(Order order) {
        // Xử lý customerPhone để tìm kiếm khách hàng
        if (order.getCustomerPhone() != null && !order.getCustomerPhone().trim().isEmpty()) {
            String cleanedPhone = order.getCustomerPhone().replaceAll("[^0-9]", "");
            if (cleanedPhone.length() < 9 || cleanedPhone.length() > 11) {
                throw new IllegalArgumentException("Số điện thoại phải từ 9 đến 11 chữ số");
            }
            Customer customer = customerRepository.findByPhone(cleanedPhone)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với số điện thoại: " + cleanedPhone));
            order.setCustomer(customer);
        } else if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại khách hàng hoặc chọn khách hàng");
        }

        // Xử lý promotionName để tìm kiếm khuyến mãi, xử lý khi có nhiều kết quả
        if (order.getPromotionName() != null && !order.getPromotionName().trim().isEmpty()) {
            List<Promotion> promotions = promotionRepository.findByNameContainingIgnoreCase(order.getPromotionName());
            if (promotions.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy khuyến mãi: " + order.getPromotionName());
            } else if (promotions.size() > 1) {
                // Chọn khuyến mãi có id lớn nhất (mới nhất)
                Promotion promotion = promotions.stream()
                        .max((p1, p2) -> p1.getId().compareTo(p2.getId()))
                        .orElseThrow(() -> new IllegalArgumentException("Nhiều khuyến mãi với tên '" + order.getPromotionName() + "', không thể xác định duy nhất."));
                order.setPromotion(promotion);
            } else {
                order.setPromotion(promotions.get(0));
            }
        }

        // Đặt customerPhone về null trước khi lưu
        order.setCustomerPhone(null);

        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy đơn hàng với ID: " + id + " để xóa.");
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countOrdersInPeriod(LocalDate startDate, LocalDate endDate) {
        return orderRepository.countByOrderDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Map<Order.OrderStatus, Long> getOrderStatusCount(LocalDate startDate, LocalDate endDate) {
        Map<Order.OrderStatus, Long> result = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.countByStatusAndOrderDateBetween(status, startDate, endDate);
            result.put(status, count);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getCustomersByMonth(LocalDate startDate) {
        List<Object[]> results = orderRepository.countCustomersByMonth(startDate);
        Map<String, Long> customersByMonth = new HashMap<>();
        for (Object[] result : results) {
            String month = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            customersByMonth.put(month, count);
        }
        return customersByMonth;
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, Order.OrderStatus newStatus) {
        if (!orderRepository.existsById(orderId)) {
            throw new NoSuchElementException("Không tìm thấy đơn hàng với ID: " + orderId);
        }
        orderRepository.updateStatus(orderId, newStatus);
    }
}