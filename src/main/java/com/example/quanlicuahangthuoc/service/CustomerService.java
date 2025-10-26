package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách hàng với ID: " + id));
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        logger.info("Tạo khách hàng với số điện thoại gốc: {}", customer.getPhone());
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + customer.getEmail());
        }
        // Làm sạch số điện thoại
        String cleanedPhone = customer.getPhone().replaceAll("[^0-9]", "");
        logger.info("Số điện thoại sau khi làm sạch: {}", cleanedPhone);
        if (customerRepository.findByPhone(cleanedPhone).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + cleanedPhone);
        }
        if (cleanedPhone.length() < 9 || cleanedPhone.length() > 11) {
            throw new IllegalArgumentException("Số điện thoại phải từ 9 đến 11 chữ số");
        }
        customer.setPhone(cleanedPhone);
        if (customer.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.vang_lai);
        }
        Customer savedCustomer = customerRepository.saveAndFlush(customer);
        logger.info("Đã lưu khách hàng với số điện thoại: {}", savedCustomer.getPhone());
        return savedCustomer;
    }

    @Transactional
    public void updateCustomer(Integer id, Customer customerDetails) {
        logger.info("Cập nhật khách hàng ID {} với số điện thoại gốc: {}", id, customerDetails.getPhone());
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách hàng với ID: " + id));
        if (!customer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + customerDetails.getEmail());
        }
        // Làm sạch số điện thoại
        String cleanedPhone = customerDetails.getPhone().replaceAll("[^0-9]", "");
        logger.info("Số điện thoại sau khi làm sạch: {}", cleanedPhone);
        if (!customer.getPhone().equals(cleanedPhone) &&
                customerRepository.findByPhone(cleanedPhone).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + cleanedPhone);
        }
        if (cleanedPhone.length() < 9 || cleanedPhone.length() > 11) {
            throw new IllegalArgumentException("Số điện thoại phải từ 9 đến 11 chữ số");
        }
        customer.setName(customerDetails.getName());
        customer.setPhone(cleanedPhone);
        customer.setEmail(customerDetails.getEmail());
        if (customerDetails.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.vang_lai);
        } else {
            customer.setCustomerType(customerDetails.getCustomerType());
        }
        customer.setRewardPoints(customerDetails.getRewardPoints());
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Đã cập nhật khách hàng với số điện thoại: {}", savedCustomer.getPhone());
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy khách hàng với ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    public Page<Customer> getAllCustomersWithPagingAndSort(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("rewardPoints".equalsIgnoreCase(sortBy) || "reward_points".equalsIgnoreCase(sortBy) || "points".equalsIgnoreCase(sortBy)) {
            sortBy = "rewardPoints";
        }
        // Không cần thay đổi sortBy vì trường trong entity là "name"
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.findAll(pageable);
    }

    public Page<Customer> searchWithPagingAndSort(String keyword, String phone, String customerType, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("rewardPoints".equalsIgnoreCase(sortBy) || "reward_points".equalsIgnoreCase(sortBy) || "points".equalsIgnoreCase(sortBy)) {
            sortBy = "rewardPoints";
        }
        // Không cần thay đổi sortBy vì trường trong entity là "name"
        
        // Clean keyword - đã được trim ở Controller, chỉ cần kiểm tra empty
        String cleanKeyword = null;
        if (keyword != null && !keyword.isEmpty()) {
            cleanKeyword = keyword;
        }
        
        // Clean phone - loại bỏ ký tự không phải số (đã được trim ở Controller)
        String cleanedPhone = null;
        if (phone != null && !phone.isEmpty()) {
            cleanedPhone = phone.replaceAll("[^0-9]", "");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.searchByNamePhoneTypeWithPaging(cleanKeyword, cleanedPhone, customerType, pageable);
    }

    public long getTotalCustomers(String keyword, String phone, String customerType) {
        // Clean phone - loại bỏ ký tự không phải số (đã được trim ở Controller)
        String cleanedPhone = null;
        if (phone != null && !phone.isEmpty()) {
            cleanedPhone = phone.replaceAll("[^0-9]", "");
        }
        
        if ((keyword != null && !keyword.isEmpty()) ||
                (cleanedPhone != null && !cleanedPhone.isEmpty()) ||
                (customerType != null && !customerType.trim().isEmpty())) {
            return customerRepository.countByFilters(keyword, cleanedPhone, customerType);
        } else {
            return customerRepository.count();
        }
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }

    @Transactional(readOnly = true)
    public long getCustomersThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        return customerRepository.countCustomersByOrdersInPeriod(startOfMonth, endOfMonth);
    }
}