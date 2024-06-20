package org.alexshin.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.model.ExchangeRate;
import org.alexshin.model.response.ErrorResponse;
import org.alexshin.repository.JDBCExchangeRatesRepository;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


@WebServlet(name = "exchangeRates", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();
        try {

            List<ExchangeRate> allExchangeRates = exchangeRatesRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(respWriter, allExchangeRates);


        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        }



    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();



    }
}
