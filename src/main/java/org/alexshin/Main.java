package org.alexshin;

import org.alexshin.model.Currency;
import org.alexshin.model.ExchangeRate;
import org.alexshin.repository.JDBCCurrencyRepository;
import org.alexshin.repository.JDBCExchangeRatesRepository;
import org.alexshin.service.ExchangeService;
import org.alexshin.util.ConfiguredDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");


        try {

            testService();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private static void testService() throws SQLException{
        ExchangeService exchangeService = new ExchangeService();
        JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();
        JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();

        var firstCur = currencyRepository.findById(6).get();
        var secCur = currencyRepository.findById(7).get();


        var er =  exchangeService.getFromCrossExchange(firstCur, secCur);
        er.ifPresent(System.out::println);


    }


    private static void testExchangeRep() throws SQLException {
        JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();

        //findById
//        var er = exchangeRatesRepository.findById(1);
//        er.ifPresent(System.out::println);


        // findAll
//        for (ExchangeRate rate : exchangeRatesRepository.findAll()) {
//                System.out.println(rate);
//            }

        // findByCode
//        var er = exchangeRatesRepository.findByCodes("USD", "RUB");
//        er.ifPresent(System.out::println);


//        var newER = new ExchangeRate(10, 1, 7, 1111);
//        System.out.println(exchangeRatesRepository.save(newER));
//        exchangeRatesRepository.update(newER);
        exchangeRatesRepository.delete(10);

    }


    private static void testCurrencyRep() throws SQLException {
        JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();

        Currency newCur = new Currency(10, "AUF", "VOLCHARA", "SS");
        //Optional<Currency> currency = currencyRepository.findById(10);
        Optional<Currency> currency = currencyRepository.findByCode("AUF");
        currency.ifPresent(System.out::println);

//            for (Currency cur : currencyRepository.findAll()) {
//                System.out.println(cur);
//            }

//            System.out.println(currencyRepository.save(newCur));
//            currencyRepository.update(newCur);
//            Optional<Currency> currency1 = currencyRepository.findById(10);
//            currency1.ifPresent(System.out::println);

    }


}