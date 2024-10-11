package br.ufrn.imd.model.dao;

import br.ufrn.imd.db.DB;
import br.ufrn.imd.model.dao.impl.CustomerDaoJDBC;
import br.ufrn.imd.model.dao.impl.ScoreDaoJDBC;

public class DaoFactory {
    public static CustomerDao createCustomerDao() {
        return new CustomerDaoJDBC(DB.getConnection());
    }

    public static ScoreDao createScoreDao() {
        return new ScoreDaoJDBC(DB.getConnection());
    }
}
