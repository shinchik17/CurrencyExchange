package org.alexshin.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.model.Currency;
import org.alexshin.model.response.ErrorResponse;
import org.alexshin.repository.JDBCCurrencyRepository;
import org.alexshin.util.Validation;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Optional;


import static org.alexshin.util.Validation.*;
import static jakarta.servlet.http.HttpServletResponse.*;


@WebServlet(name = "currencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();
        String currencyCode;

        if (req.getPathInfo() != null) {
            currencyCode = req.getPathInfo().replace("/", "").toUpperCase();
        } else {
            currencyCode = "";
        }

        if (!isValidCurrencyCode(currencyCode)) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(respWriter,
                    new ErrorResponse(SC_BAD_REQUEST, "Incorrect currency code"));
            return;
        }

        try {
            Optional<Currency> optionalCurrency = currencyRepository.findByCode(currencyCode);
            if (optionalCurrency.isEmpty()) {
                resp.setStatus(SC_BAD_REQUEST);
                objectMapper.writeValue(respWriter,
                        new ErrorResponse(SC_NOT_FOUND, "Currency not found"));
                return;
            }

            resp.setStatus(200);
            objectMapper.writeValue(respWriter, optionalCurrency.get());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



}
