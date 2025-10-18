package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort;
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
                .orElseThrow(() -> new RuntimeException("Cannot find customer id: " + id));
    }
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already existed");
        }

        if (customer.getRewardPoints() == null) {
            customer.setRewardPoints(0);
        }

        return customerRepository.save(customer);
    }
    public Customer updateCustomer(Integer id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can not find customer id: " + id));

        if (!customer.getEmail().equals(customerDetails.getEmail())
                && customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new RuntimeException("Email already exist");
        }

        customer.setName(customerDetails.getName());
        customer.setPhone(customerDetails.getPhone());
        customer.setEmail(customerDetails.getEmail());
        customer.setCustomerType(customerDetails.getCustomerType());
        customer.setRewardPoints(customerDetails.getRewardPoints());

        return customerRepository.save(customer);
    }
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find customer id: " + id));

        customerRepository.deleteById(id);
    }
    public Page<Customer> getAllCustomersWithPagingAndSort(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.findAll(pageable);
    }

    public Page<Customer> searchWithPagingAndSort(String keyword, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        if (keyword == null || keyword.trim().isEmpty()) {
            return customerRepository.findAll(pageable);
        }

        return customerRepository.searchByNameOrPhoneWithPaging(keyword.trim(), pageable);
    }
}