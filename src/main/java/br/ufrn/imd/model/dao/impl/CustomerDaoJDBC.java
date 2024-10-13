package br.ufrn.imd.model.dao.impl;

import br.ufrn.imd.db.DB;
import br.ufrn.imd.db.DbException;
import br.ufrn.imd.model.dao.CustomerDao;
import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoJDBC implements CustomerDao {
    private final Connection conn;

    public CustomerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Customer customer) {
        insert(customer, new Score(300, 300, 300, 300));
    }

    @Override
    public void insert(Customer customer, Score score) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement(
                    "INSERT INTO Customers "
                            + "VALUES "
                            + "(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, customer.getId());
            st.setString(2, customer.getSsn());
            st.setDate(3, Date.valueOf(customer.getSignupDate()));

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();

                DB.closeResultSet(rs);
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }

            st = conn.prepareStatement(
                    "INSERT INTO Scores "
                            + "VALUES "
                            + "(?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, customer.getId());
            st.setLong(2, 1L);
            st.setString(3, Instant.now().toString());
            st.setInt(4, score.getPaymentHistoryScore());
            st.setInt(5, score.getCreditUtilizationScore());
            st.setInt(6, score.getAmountScore());
            st.setInt(7, score.getAvailableCreditScore());

            rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();

                DB.closeResultSet(rs);
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Customer findBySsn(String ssn) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try{
            st = conn.prepareStatement("SELECT * FROM Customers "
                    + "WHERE ssn = ?");

            st.setString(1, ssn);
            rs = st.executeQuery();

            if(rs.next()){
                return instantiateCustomer(rs);
            }

            return null;
        } catch(SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public void deleteBySsn(String ssn) {
        Customer customer = findBySsn(ssn);
        PreparedStatement st = null;

        try{
            st = conn.prepareStatement("DELETE FROM Scores WHERE customer_id = ?");

            st.setString(1, customer.getId());

            st.executeUpdate();

            st = conn.prepareStatement("DELETE FROM Customers WHERE ssn = ?");

            st.setString(1, ssn);

            st.executeUpdate();
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public List<Customer> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try{
            st = conn.prepareStatement(
                    "SELECT * FROM Customers "
                            + "ORDER BY ssn");

            rs = st.executeQuery();

            List<Customer> customers = new ArrayList<>();

            while(rs.next()){
                customers.add(instantiateCustomer(rs));
            }

            return customers;
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Customer instantiateCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer(rs.getString("ssn"), rs.getDate("signup_date").toLocalDate());

        return customer;
    }
}
