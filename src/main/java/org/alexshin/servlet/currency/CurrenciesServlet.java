package org.alexshin.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.model.ErrorResponse;
import org.alexshin.model.entity.Currency;
import org.alexshin.repository.JDBCCurrencyRepository;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.alexshin.util.Validation.isValidCurrencyCode;
import static org.alexshin.util.Validation.isValidString;


@WebServlet(name = "currenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        try {

            List<Currency> allCurrencies = currencyRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(respWriter, allCurrencies);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }


    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        String errorMessage = "";
        if (!isValidString(name)) {
            errorMessage = String.format("Invalid currency name: <%s>", name);
        } else if (!isValidCurrencyCode(code)) {
            errorMessage = String.format("Invalid currency code: <%s>", code);
        } else if (!isValidString(sign)) {
            errorMessage = String.format("Invalid currency sign: <%s>", sign);
        }

        if (!errorMessage.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
            return;
        }


        try {

            code = code.toUpperCase();

            if (currencyRepository.findByCode(code).isPresent()) {
                resp.setStatus(SC_CONFLICT);
                errorMessage = "Currency with such code already exists";
                objectMapper.writeValue(respWriter, new ErrorResponse(SC_CONFLICT, errorMessage));
                return;
            }

            Currency currency = new Currency(code, name, sign);
            int generatedID = currencyRepository.save(currency);
            currency.setId(generatedID);

            resp.setStatus(SC_OK);
            objectMapper.writeValue(respWriter, currency);

        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }


    }


}
