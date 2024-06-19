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

import static org.alexshin.util.Validation.*;
import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;


@WebServlet(name = "currenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;utf-8");
        Writer respWriter = resp.getWriter();

        try {

            List<Currency> allCurrencies = currencyRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(respWriter, allCurrencies);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;utf-8");
        Writer respWriter = resp.getWriter();

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        String errorMessage = "";
        if (!isValidString(name)) {
            errorMessage = "Invalid currency name: " + name;
        } else if (!isValidCurrencyCode(code)) {
            errorMessage = "Invalid currency code: " + code;
        } else if (!isValidString(sign)) {
            errorMessage = "Invalid currency sign: " + sign;
        }

        if (!errorMessage.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
            return;
        }





    }


}
