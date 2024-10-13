package br.ufrn.imd.application;

import br.ufrn.imd.model.dao.CustomerDao;
import br.ufrn.imd.model.dao.DaoFactory;
import br.ufrn.imd.model.dao.ScoreDao;
import br.ufrn.imd.model.entities.Customer;

public class Program {
    public static void main(String[] args) {
        CustomerDao customerDao = DaoFactory.createCustomerDao();
        ScoreDao scoreDao = DaoFactory.createScoreDao();


    }
}
