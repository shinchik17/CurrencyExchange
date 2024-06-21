package org.alexshin.servlet.ExchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.model.ExchangeRate;
import org.alexshin.model.response.ErrorResponse;
import org.alexshin.repository.JDBCCurrencyRepository;
import org.alexshin.repository.JDBCExchangeRatesRepository;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.alexshin.util.Validation.isValidCurrencyCode;
import static org.alexshin.util.Validation.isValidRate;


@WebServlet(name = "exchangeRates", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();
    private static final JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        try {
            List<ExchangeRate> allExchangeRates = exchangeRatesRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(respWriter, allExchangeRates);

        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        String errorMessage = "";
        if (!isValidCurrencyCode(baseCurrencyCode)) {
            errorMessage = String.format("Invalid base currency code: <%s>", baseCurrencyCode);
        } else if (!isValidCurrencyCode(targetCurrencyCode)) {
            errorMessage = String.format("Invalid target currency code: <%s>", targetCurrencyCode);
        } else if (!isValidRate(rate)) {
            errorMessage = String.format("Invalid currency sign: <%s>", rate);
        }

        if (!errorMessage.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
            return;
        }

        try {

            ExchangeRate exchangeRate = new ExchangeRate(
                    currencyRepository.findByCode(baseCurrencyCode).orElseThrow(),
                    currencyRepository.findByCode(baseCurrencyCode).orElseThrow(),
                    Double.parseDouble(rate)
            );

            int generatedID = exchangeRatesRepository.save(exchangeRate);
            exchangeRate.setId(generatedID);

            resp.setStatus(SC_CREATED);
            objectMapper.writeValue(respWriter, exchangeRate);


        } catch (SQLException e) {

            // TODO: распарсить случай, когда курс уже существует
            if (((SQLiteException) e).getResultCode().name().equals("SQLITE_CONSTRAINT_UNIQUE")) {
                resp.setStatus(SC_CONFLICT);
                errorMessage = "Specified exchange rate already exists";
                objectMapper.writeValue(respWriter, new ErrorResponse(SC_CONFLICT, errorMessage));
                return;
            }

            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));

        } catch (NoSuchElementException e) {
            resp.setStatus(SC_NOT_FOUND);
            errorMessage = "One or both of given currencies do not represented in database";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_NOT_FOUND, errorMessage));
        }


    }
}
