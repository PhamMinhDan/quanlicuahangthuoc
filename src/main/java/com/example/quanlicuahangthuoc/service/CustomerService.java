package com.example.quanlicuahangthuoc.service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getAllCustomers(String sortBy, String sortDir) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "name";
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }

        if ("firstCharLastWord".equalsIgnoreCase(sortBy)) {
            List<Customer> all = customerRepository.findAll();
            Comparator<Customer> cmp = Comparator.comparing(c -> {
                String name = c.getName();
                if (name == null || name.trim().isEmpty()) return "";
                String[] parts = name.trim().split("\\s+");
                String lastWord = parts.length > 0 ? parts[parts.length - 1] : "";
                return lastWord.isEmpty() ? "" : lastWord.substring(0, 1).toLowerCase();
            });
            if (direction == Sort.Direction.DESC) {
                cmp = cmp.reversed();
            }
            all.sort(cmp);
            return all;
        }

        if ("rewardPoints".equalsIgnoreCase(sortBy) || "reward_points".equalsIgnoreCase(sortBy) || "points".equalsIgnoreCase(sortBy)) {
            sortBy = "rewardPoints";
        }
        return customerRepository.findAll(Sort.by(direction, sortBy));
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
                .orElseThrow(() -> new RuntimeException("Cannot find customer id: " + id));
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
        if ("firstCharLastWord".equalsIgnoreCase(sortBy)) {
            Page<Customer> pageResult = customerRepository.findAll(PageRequest.of(page, size));
            List<Customer> sortedList = pageResult.getContent();
            Comparator<Customer> cmp = Comparator.comparing(c -> {
                String name = c.getName();
                if (name == null || name.trim().isEmpty()) return "";
                String[] parts = name.trim().split("\\s+");
                String lastWord = parts.length > 0 ? parts[parts.length - 1] : "";
                return lastWord.isEmpty() ? "" : lastWord.substring(0, 1).toLowerCase();
            });
            if (sortDirection == Sort.Direction.DESC) {
                cmp = cmp.reversed();
            }
            sortedList.sort(cmp);
            return new PageImpl<>(sortedList, PageRequest.of(page, size), pageResult.getTotalElements());
        }
        if ("rewardPoints".equalsIgnoreCase(sortBy) || "reward_points".equalsIgnoreCase(sortBy) || "points".equalsIgnoreCase(sortBy)) {
            sortBy = "rewardPoints";
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return customerRepository.findAll(pageable);
    }

    public Page<Customer> searchWithPagingAndSort(String keyword, String phone, String customerType, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("firstCharLastWord".equalsIgnoreCase(sortBy)) {
            Page<Customer> pageResult = customerRepository.searchByNamePhoneTypeWithPaging(keyword, phone, customerType, PageRequest.of(page, size));
            List<Customer> sortedList = pageResult.getContent();
            Comparator<Customer> cmp = Comparator.comparing(c -> {
                String name = c.getName();
                if (name == null || name.trim().isEmpty()) return "";
                String[] parts = name.trim().split("\\s+");
                String lastWord = parts.length > 0 ? parts[parts.length - 1] : "";
                return lastWord.isEmpty() ? "" : lastWord.substring(0, 1).toLowerCase();
            });
            if (sortDirection == Sort.Direction.DESC) {
                cmp = cmp.reversed();
            }
            sortedList.sort(cmp);
            return new PageImpl<>(sortedList, PageRequest.of(page, size), pageResult.getTotalElements());
        }
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