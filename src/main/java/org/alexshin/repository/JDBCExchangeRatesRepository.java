package org.alexshin.repository;

import org.alexshin.model.Currency;
import org.alexshin.model.ExchangeRate;
import org.alexshin.util.ConfiguredDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCExchangeRatesRepository implements IRepository<ExchangeRate> {
    private final ConfiguredDB db = new ConfiguredDB();

    @Override
    public List<ExchangeRate> findAll() throws SQLException {

        try (var connection = db.getConnection()) {
            List<ExchangeRate> rateList = new ArrayList<>();

            String queryString = "SELECT * FROM main.ExchangeRates";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);

            while (resultSet.next()) {
                var rate = getExchangeRate(resultSet);
                rateList.add(rate);
            }
            return rateList;
        }
    }


    // TODO: подумать об исключениях. Возможно, тут всё-таки надо ловить их и бросать вручную
    @Override
    public Optional<ExchangeRate> findById(int id) throws SQLException {

        try (var connection = db.getConnection()) {

            String queryString = "SELECT * FROM ExchangeRates WHERE id=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, id);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(getExchangeRate(resultSet));
        }
    }


    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {

        try (var connection = db.getConnection()) {
            String preQueryString =
                    "SELECT er.id as er_id," +
                    "base_cur.Code as bc_code, " +
                    "target_cur.Code as tc_code " +
                    "FROM ExchangeRates as er " +
                    "   JOIN Currencies as base_cur ON er.BaseCurrencyId = base_cur.id " +
                    "   JOIN Currencies as target_cur ON er.TargetCurrencyId = target_cur.ID " +
                    "WHERE bc_code=? AND tc_code = ?";

            PreparedStatement stmt = connection.prepareStatement(preQueryString);
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            int exchangeRateId = resultSet.getInt(1);
            return findById(exchangeRateId);
        }
    }

    public Optional<ExchangeRate> findByUsdBase(String targetCurrencyCode) throws SQLException {
        String usdCode = "USD";
        return findByCodes(usdCode, targetCurrencyCode);
    }

    public Optional<ExchangeRate> findByUsdTarget(String targetCurrencyCode) throws SQLException {
        String usdCode = "USD";
        return findByCodes(targetCurrencyCode, usdCode);
    }


    @Override
    public int save(ExchangeRate entity) throws SQLException {
        try (var connection = db.getConnection()) {

            String queryString = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, entity.getBaseCurrencyId());
            stmt.setInt(2, entity.getTargetCurrencyId());
            stmt.setDouble(3, entity.getRate());

            return stmt.executeUpdate();
        }
    }

    @Override
    public void update(ExchangeRate entity) throws SQLException {

        try (var connection = db.getConnection()) {

            // TODO: стоит ли сделать доп. запрос, чтобы обновлять запись, без знания её ID?
//            String preQueryString = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId=? AND TargetCurrencyId=?";
//            PreparedStatement prestmt = connection.prepareStatement(preQueryString);
//            prestmt.setInt(1, object.getBaseCurrencyId());
//            prestmt.setInt(2, object.getTargetCurrencyId());
//            ResultSet resultSet = prestmt.executeQuery();


            String queryString = "UPDATE ExchangeRates " +
                    "SET BaseCurrencyId  = ?, TargetCurrencyId = ?, Rate = ? " +
                    "WHERE id=?";

            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, entity.getBaseCurrencyId());
            stmt.setInt(2, entity.getTargetCurrencyId());
            stmt.setDouble(3, entity.getRate());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {

        try (var connection = db.getConnection()) {

            String queryString = "DELETE FROM ExchangeRates WHERE id=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getDouble(4)
        );
    }

}
