package br.ufrn.imd.model.dao;

import br.ufrn.imd.model.entities.Score;
import br.ufrn.imd.model.entities.VersionedKey;

import java.util.NavigableMap;

public interface ScoreDao {
    void insert(String customerSsn, Score score);
//    void update(String customerSsn, long version, Score score);
    void delete(String customerSsn, long version);
    NavigableMap<VersionedKey, Score> findByCustomerSsn(String customerSsn);
}
