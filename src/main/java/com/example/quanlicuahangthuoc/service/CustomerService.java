package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerService {

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
    public void createCustomer(Customer customer) {
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
        if (customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + customer.getPhone());
        }
        // Loại bỏ dấu phẩy hoặc ký tự không mong muốn khỏi số điện thoại
        String cleanedPhone = customer.getPhone().replaceAll("[^0-9]", ""); // Chỉ giữ số
        if (cleanedPhone.length() > 11 || cleanedPhone.length() < 9) {
            throw new IllegalArgumentException("Số điện thoại phải từ 9 đến 11 chữ số");
        }
        customer.setPhone(cleanedPhone);
        // Đặt giá trị mặc định cho customerType nếu null
        if (customer.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.vang_lai); // Mặc định là "Vãng lai"
        }
        customerRepository.save(customer);
    }

    @Transactional
    public void updateCustomer(Integer id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách hàng với ID: " + id));
        if (!customer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + customerDetails.getEmail());
        }
        if (!customer.getPhone().equals(customerDetails.getPhone()) &&
                customerRepository.findByPhone(customerDetails.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + customerDetails.getPhone());
        }

        customer.setName(customerDetails.getName());
        // Loại bỏ dấu phẩy hoặc ký tự không mong muốn khỏi số điện thoại
        String cleanedPhone = customerDetails.getPhone().replaceAll("[^0-9]", ""); // Chỉ giữ số
        if (cleanedPhone.length() > 11 || cleanedPhone.length() < 9) {
            throw new IllegalArgumentException("Số điện thoại phải từ 9 đến 11 chữ số");
        }
        customer.setPhone(cleanedPhone);
        customer.setEmail(customerDetails.getEmail());
        // Đặt giá trị mặc định cho customerType nếu null
        if (customerDetails.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.vang_lai); // Mặc định là "Vãng lai"
        } else {
            customer.setCustomerType(customerDetails.getCustomerType());
        }
        // Giữ nguyên rewardPoints từ customerDetails, không reset về 0
        customer.setRewardPoints(customerDetails.getRewardPoints());
        customerRepository.save(customer);
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.findAll(pageable);
    }

    public Page<Customer> searchWithPagingAndSort(String keyword, String phone, String customerType, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("rewardPoints".equalsIgnoreCase(sortBy) || "reward_points".equalsIgnoreCase(sortBy) || "points".equalsIgnoreCase(sortBy)) {
            sortBy = "rewardPoints";
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.searchByNamePhoneTypeWithPaging(keyword, phone, customerType, pageable);
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }
}