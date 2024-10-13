package br.ufrn.imd.model.dao;

import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;

import java.util.List;

public interface CustomerDao {
    void insert(Customer customer);
    void insert(Customer customer, Score score);
    Customer findBySsn(String ssn);
    void deleteBySsn(String ssn);
    List<Customer> findAll();
}
