package br.ufrn.imd.model.dao.impl;

import br.ufrn.imd.db.DB;
import br.ufrn.imd.db.DbException;
import br.ufrn.imd.model.dao.DaoFactory;
import br.ufrn.imd.model.dao.ScoreDao;
import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;
import br.ufrn.imd.model.entities.VersionedKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ScoreDaoJDBC implements ScoreDao {
    private Connection conn;

    public ScoreDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(String customerSsn, Score score) {
        Customer customer = DaoFactory.createCustomerDao().findBySsn(customerSsn);
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT COUNT(*) FROM Scores "
            + "WHERE customer_id=?");

            st.setString(1, customer.getId());

            rs = st.executeQuery();

            long nextVersionNumber = 0L;

            if(rs.next()){
                nextVersionNumber = rs.getLong(1);
            }

            st = conn.prepareStatement(
                    "INSERT INTO Scores "
                            + "VALUES "
                            + "(?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, customer.getId());
            st.setLong(2, nextVersionNumber + 1);
            st.setString(3, Instant.now().toString());
            st.setInt(4, score.getPaymentHistoryScore());
            st.setInt(5, score.getCreditUtilizationScore());
            st.setInt(6, score.getAmountScore());
            st.setInt(7, score.getAvailableCreditScore());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                rs = st.getGeneratedKeys();

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
    public void delete(String customerSsn, long version) {
        Customer customer = DaoFactory.createCustomerDao().findBySsn(customerSsn);
        PreparedStatement st = null;

        try{
            st = conn.prepareStatement("DELETE FROM Scores WHERE customer_id = ? AND version = ?");

            st.setString(1, customer.getId());
            st.setLong(2, version);

            st.executeUpdate();
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public NavigableMap<VersionedKey, Score> findByCustomerSsn(String customerSsn) {
        Customer customer = DaoFactory.createCustomerDao().findBySsn(customerSsn);
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM Scores "
                    + "WHERE customer_id=?"
            );

            st.setString(1, customer.getId());

            rs = st.executeQuery();

            NavigableMap<VersionedKey, Score> keyScore = new ConcurrentSkipListMap<>();

            while(rs.next()){
                keyScore.put(instantiateVersionedKey(rs), instantiateScore(rs));
            }

            return keyScore;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public NavigableMap<VersionedKey, Score> findLastScoreByCustomerSsn(String customerSsn) {
        Customer customer = DaoFactory.createCustomerDao().findBySsn(customerSsn);
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM Scores "
                    + "WHERE customer_id=? "
                    + "ORDER BY version DESC "
                    + "LIMIT 1"
            );

            st.setString(1, customer.getId());

            rs = st.executeQuery();

            NavigableMap<VersionedKey, Score> keyScore = new ConcurrentSkipListMap<>();

            while(rs.next()){
                keyScore.put(instantiateVersionedKey(rs), instantiateScore(rs));
            }

            return keyScore;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    private VersionedKey instantiateVersionedKey(ResultSet rs) throws SQLException {
        return new VersionedKey(rs.getString("key"), rs.getLong("version"));
    }

    private Score instantiateScore(ResultSet rs) throws SQLException {
        return new Score(rs.getInt("payment_history_score"), rs.getInt("credit_utilization_score"), rs.getInt("amount_score"), rs.getInt("available_credit_score"));
    }
}
