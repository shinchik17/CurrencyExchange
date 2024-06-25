package org.alexshin.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebFilter(filterName = "baseFilter", urlPatterns = "/*")
public class FilterServlet extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        res.setContentType("application/json;utf-8");

        ((HttpServletResponse) res).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) res).addHeader("Access-Control-Allow-Methods", "GET, PUT, PATCH, HEAD, OPTIONS, POST");
        ((HttpServletResponse) res).addHeader("Access-Control-Allow-Headers", "*");



        chain.doFilter(req, res);

    }
}
