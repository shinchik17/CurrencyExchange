package org.alexshin.servlet.Exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alexshin.DTO.ErrorResponse;
import org.alexshin.DTO.ExchangeResponse;
import org.alexshin.service.ExchangeService;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.alexshin.util.Validation.isValidCurrencyCode;
import static org.alexshin.util.Validation.isValidRate;

@WebServlet(name = "exchange", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeService exchangeService = new ExchangeService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer respWriter = resp.getWriter();

        String baseCode = req.getParameter("from");
        String targetCode = req.getParameter("to");
        String amountString = req.getParameter("amount");

        String errorMessage = "";
        if (!isValidCurrencyCode(baseCode)) {
            errorMessage = String.format("Invalid base currency code: <%s>", baseCode);
        } else if (!isValidCurrencyCode(targetCode)) {
            errorMessage = String.format("Invalid target currency code: <%s>", targetCode);
        } else if (!isValidRate(amountString)) {
            errorMessage = String.format("Invalid amount: <%s>", amountString);
        }

        if (!errorMessage.isBlank()) {
            resp.setStatus(SC_BAD_REQUEST);
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_BAD_REQUEST, errorMessage));
            return;
        }

        try {

            ExchangeResponse exchangeResponse = exchangeService.getExchangeResponse(
                    baseCode.toUpperCase(),
                    targetCode.toUpperCase(),
                    Double.parseDouble(amountString));


            resp.setStatus(SC_OK);
            objectMapper.writeValue(respWriter, exchangeResponse);


        } catch (SQLException e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            errorMessage = "Database is unavailable";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_INTERNAL_SERVER_ERROR, errorMessage));
        } catch (NoSuchElementException e) {
            resp.setStatus(SC_NOT_FOUND);
            errorMessage = "Exchange rate for given currencies not found";
            objectMapper.writeValue(respWriter, new ErrorResponse(SC_NOT_FOUND, errorMessage));
        }


    }
}
