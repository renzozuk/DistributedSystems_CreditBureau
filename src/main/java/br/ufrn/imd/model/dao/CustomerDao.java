package br.ufrn.imd.model.dao;

import br.ufrn.imd.model.entities.Customer;

import java.util.List;

public interface CustomerDao {
    void insert(Customer customer);
    void update(Customer customer);
    void deleteById(Customer customer);
    Customer findById(int id);
    List<Customer> findAll();
}
