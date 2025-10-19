package com.example.quanlicuahangthuoc.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.quanlicuahangthuoc.entity.Customer;
import com.example.quanlicuahangthuoc.repository.CustomerRepository;

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
        // If caller requests sorting by the last token of the name (e.g. last name),
        // perform in-memory sorting because it's not a mapped column.
        if ("lastName".equalsIgnoreCase(sortBy) || "last_name".equalsIgnoreCase(sortBy)) {
            List<Customer> all = customerRepository.findAll();
            java.util.Comparator<Customer> cmp = java.util.Comparator.comparing(c -> {
                String n = c.getName();
                if (n == null) return "";
                String[] parts = n.trim().split("\\s+");
                return parts.length == 0 ? "" : parts[parts.length - 1];
            }, String.CASE_INSENSITIVE_ORDER);
            if (direction == Sort.Direction.DESC) {
                cmp = cmp.reversed();
            }
            all.sort(cmp);
            return all;
        }

        Sort sort = Sort.by(direction, sortBy);
        return customerRepository.findAll(sort);
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find customer id: " + id));
    }

}