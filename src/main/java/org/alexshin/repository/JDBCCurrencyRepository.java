package org.alexshin.repository;


import org.alexshin.model.Currency;
import org.alexshin.util.ConfiguredDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCCurrencyRepository implements IRepository<Currency> {
    private final ConfiguredDB db = new ConfiguredDB();

    @Override
    public List<Currency> findAll() throws SQLException {

        try (var connection = db.getConnection()) {
            List<Currency> currencyList = new ArrayList<>();

            String queryString = "SELECT * FROM Currencies";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);

            while (resultSet.next()) {
                Currency cur = getCurrency(resultSet);
                currencyList.add(cur);
            }
            return currencyList;
        }
    }

    @Override
    public Optional<Currency> findById(int id) throws SQLException {

        try (var connection = db.getConnection()) {
            String queryString = "SELECT * FROM Currencies WHERE id=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, id);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(getCurrency(resultSet));
        }
    }


    public Optional<Currency> findByCode(String code) throws SQLException {

        try (var connection = db.getConnection()) {
            String queryString = "SELECT * FROM Currencies WHERE Code=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setString(1, code);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(getCurrency(resultSet));
        }
    }

    @Override
    public int save(Currency entity) throws SQLException {

        try (var connection = db.getConnection()) {

            String queryString = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(queryString, PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setString(1, entity.getCode());
            stmt.setString(2, entity.getFullName());
            stmt.setString(3, entity.getSign());

            int rowAffected = stmt.executeUpdate();

            if (rowAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Failed to retrieve generated ID");

            }

            throw new SQLException("No records inserted");

        }
    }

    @Override
    public void update(Currency entity) throws SQLException {

        try (var connection = db.getConnection()) {

            String queryString = "UPDATE Currencies " +
                    "SET Code  = ?, FullName = ?, Sign = ? " +
                    "WHERE id=?";

            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setString(1, entity.getCode());
            stmt.setString(2, entity.getFullName());
            stmt.setString(3, entity.getSign());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {

        try (var connection = db.getConnection()) {

            String queryString = "DELETE FROM Currencies WHERE id=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    private static Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
        );
    }


}
