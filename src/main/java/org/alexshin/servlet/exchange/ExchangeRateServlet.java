package org.alexshin.servlet.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.model.ErrorResponse;
import org.alexshin.model.entity.ExchangeRate;
import org.alexshin.repository.JDBCExchangeRatesRepository;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.alexshin.util.Validation.isValidExchangeRateString;
import static org.alexshin.util.Validation.isValidRate;

@WebServlet(name = "exchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();
        String exchangeRateString = "";
        if (req.getPathInfo() != null) {
            exchangeRateString = req.getPathInfo().replace("/", "").toUpperCase();
        }

        if (!isValidExchangeRateString(exchangeRateString)) {
            resp.setStatus(SC_BAD_REQUEST);
            String errorMessage = "One or both of given currency codes are invalid";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));

        }

        try {
            Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCodes(
                    exchangeRateString.substring(0, 3),
                    exchangeRateString.substring(3));

            if (exchangeRate.isEmpty()) {
                resp.setStatus(SC_NOT_FOUND);
                String errorMessage = "Exchange rate for given currencies not found";
                objectMapper.writeValue(respWriter, new ErrorResponse(SC_NOT_FOUND, errorMessage));
                return;
            }

            resp.setStatus(SC_OK);
            objectMapper.writeValue(respWriter, exchangeRate.get());


        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }


    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        String rateString = "";
        String exchangeRateString = "";

        if (req.getPathInfo() != null) {
            exchangeRateString = req.getPathInfo().replace("/", "").substring(0, 6).toUpperCase();
            rateString = req.getReader().readLine().substring(5);
        }


        if (!isValidExchangeRateString(exchangeRateString)) {
            resp.setStatus(SC_BAD_REQUEST);
            String errorMessage = "One or both of given currency codes are invalid";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
        }

        if (!isValidRate(rateString)) {
            resp.setStatus(SC_BAD_REQUEST);
            String errorMessage = "Exchange rate is invalid";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
        }


        try {

            Optional<ExchangeRate> optExchangeRate = exchangeRatesRepository.findByCodes(
                    exchangeRateString.substring(0, 3),
                    exchangeRateString.substring(3));

            if (optExchangeRate.isEmpty()) {
                resp.setStatus(SC_NOT_FOUND);
                String errorMessage = "Exchange rate for given currencies not found";
                objectMapper.writeValue(respWriter, new ErrorResponse(SC_NOT_FOUND, errorMessage));
                return;
            }

            ExchangeRate exchangeRate = optExchangeRate.get();
            exchangeRate.setRate(BigDecimal.valueOf(Double.parseDouble(rateString)));
            exchangeRatesRepository.update(exchangeRate);

            resp.setStatus(SC_OK);
            objectMapper.writeValue(respWriter, exchangeRate);


        } catch (Exception e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }


    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            this.doPatch(req, resp);
        } else {
            super.service(req, resp);
        }

    }
}
