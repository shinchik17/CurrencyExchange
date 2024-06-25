package org.alexshin.repository;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    List<T> findAll() throws SQLException;

    Optional<T> findById(int id) throws SQLException;

    int save(T entity) throws SQLException;

    void update(T entity) throws SQLException;

    void delete(int id) throws SQLException;

}
