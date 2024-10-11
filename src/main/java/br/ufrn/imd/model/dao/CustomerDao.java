package br.ufrn.imd.model.dao;

import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;

import java.util.List;

public interface CustomerDao {
    void insert(Customer customer);
    void insert(Customer customer, Score score);
    void deleteById(String id);
    void deleteBySsn(String ssn);
    Customer findById(String id);
    Customer findBySsn(String id);
    List<Customer> findAll();
}
