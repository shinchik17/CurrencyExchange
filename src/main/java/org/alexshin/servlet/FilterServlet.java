package org.alexshin.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;

import java.io.IOException;


@WebFilter(filterName = "baseFilter", urlPatterns = "/*")
public class FilterServlet extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        super.doFilter(req, res, chain);

        res.setContentType("application/json;utf-8");
        chain.doFilter(req, res);

    }
}
