package org.alexshin.repository;

import org.alexshin.model.entity.Currency;
import org.alexshin.model.entity.ExchangeRate;
import org.alexshin.util.ConfiguredDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JDBCExchangeRatesRepository implements Repository<ExchangeRate> {
//    private final ConfiguredDB db = new ConfiguredDB();
//    private final ConfiguredDB ConfiguredDataSource = new ConfiguredDB();

    @Override
    public List<ExchangeRate> findAll() throws SQLException {

        try (var connection = ConfiguredDataSource.getConnection()) {
            List<ExchangeRate> rateList = new ArrayList<>();

            String queryString = """
                    SELECT base_cur.ID         as bc_id,
                           base_cur.Code       as bc_code,
                           base_cur.FullName   as bc_name,
                           base_cur.Sign       as bc_sign,
                           target_cur.ID       as tc_id,
                           target_cur.Code     as tc_code,
                           target_cur.FullName as tc_name,
                           target_cur.Sign     as tc_sign,
                           er.ID               as er_id,
                           er.Rate             as rate
                    FROM ExchangeRates as er
                             JOIN Currencies as base_cur ON er.BaseCurrencyId = base_cur.ID
                             JOIN Currencies as target_cur ON er.TargetCurrencyId = target_cur.ID
                    """;

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);

            while (resultSet.next()) {
                var rate = getExchangeRate(resultSet);
                rateList.add(rate);
            }
            return rateList;
        }
    }

    @Override
    public Optional<ExchangeRate> findById(int id) throws SQLException {

        try (var connection = ConfiguredDataSource.getConnection()) {

            String queryString = """
                    SELECT base_cur.ID         as bc_id,
                           base_cur.Code       as bc_code,
                           base_cur.FullName   as bc_name,
                           base_cur.Sign       as bc_sign,
                           target_cur.ID       as tc_id,
                           target_cur.Code     as tc_code,
                           target_cur.FullName as tc_name,
                           target_cur.Sign     as tc_sign,
                           er.ID               as er_id,
                           er.Rate             as rate
                    FROM ExchangeRates as er
                             JOIN Currencies as base_cur ON er.BaseCurrencyId = base_cur.ID
                             JOIN Currencies as target_cur ON er.TargetCurrencyId = target_cur.ID
                    WHERE er.ID = ?
                    """;
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

        try (var connection = ConfiguredDataSource.getConnection()) {
            String queryString = """
                    SELECT base_cur.ID         as bc_id,
                           base_cur.Code       as bc_code,
                           base_cur.FullName   as bc_name,
                           base_cur.Sign       as bc_sign,
                           target_cur.ID       as tc_id,
                           target_cur.Code     as tc_code,
                           target_cur.FullName as tc_name,
                           target_cur.Sign     as tc_sign,
                           er.ID               as er_id,
                           er.Rate             as rate
                    FROM ExchangeRates as er
                             JOIN Currencies as base_cur ON er.BaseCurrencyId = base_cur.ID
                             JOIN Currencies as target_cur ON er.TargetCurrencyId = target_cur.ID
                    WHERE bc_code = ? AND tc_code = ?
                    """;

            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(getExchangeRate(resultSet));

//            int exchangeRateId = resultSet.getInt(1);
//            return findById(exchangeRateId);
        }
    }


    public List<ExchangeRate> findByCodesWithUsdBase(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {

        try (var connection = ConfiguredDataSource.getConnection()) {

            String preQueryString = """
                    SELECT base_cur.ID         as bc_id,
                           base_cur.Code       as bc_code,
                           base_cur.FullName   as bc_name,
                           base_cur.Sign       as bc_sign,
                           target_cur.ID       as tc_id,
                           target_cur.Code     as tc_code,
                           target_cur.FullName as tc_name,
                           target_cur.Sign     as tc_sign,
                           er.ID               as er_id,
                           er.Rate             as rate
                    FROM ExchangeRates as er
                             JOIN Currencies as base_cur ON er.BaseCurrencyId = base_cur.ID
                             JOIN Currencies as target_cur ON er.TargetCurrencyId = target_cur.ID
                    WHERE bc_code = ? AND tc_code IN (?, ?)
                    """;

            PreparedStatement stmt = connection.prepareStatement(preQueryString);
            stmt.setString(1, "USD");
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);
            stmt.execute();

            ResultSet resultSet = stmt.getResultSet();
            List<ExchangeRate> exchangeRateList = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRateList.add(getExchangeRate(resultSet));
            }


            return exchangeRateList;

//            int exchangeRateId = resultSet.getInt(1);
//            return findById(exchangeRateId);
        }

    }


    @Override
    public int save(ExchangeRate entity) throws SQLException {
        try (var connection = ConfiguredDataSource.getConnection()) {

            String queryString = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, entity.getBaseCurrency().getId());
            stmt.setInt(2, entity.getTargetCurrency().getId());
            stmt.setBigDecimal(3, entity.getRate());

            int rowAffected = stmt.executeUpdate();

            if (rowAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Failed to retrieve generated ID");
            }

            throw new SQLException("No records inserted");
        }
    }

    @Override
    public void update(ExchangeRate entity) throws SQLException {

        try (var connection = ConfiguredDataSource.getConnection()) {

            String queryString = "UPDATE ExchangeRates " +
                    "SET BaseCurrencyId  = ?, TargetCurrencyId = ?, Rate = ? " +
                    "WHERE id=?";

            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, entity.getBaseCurrency().getId());
            stmt.setInt(2, entity.getTargetCurrency().getId());
            stmt.setBigDecimal(3, entity.getRate());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {

        try (var connection = ConfiguredDataSource.getConnection()) {

            String queryString = "DELETE FROM ExchangeRates WHERE id=?";
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

    }


    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {

        return new ExchangeRate(
                resultSet.getInt(9),
                new Currency(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ),
                new Currency(
                        resultSet.getInt(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8)
                ),
                resultSet.getBigDecimal(10)
        );
    }

}
