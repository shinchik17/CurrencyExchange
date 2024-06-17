INSERT INTO Currencies (Code, FullName, Sign) VALUES ('USD', 'US Dollar', '$');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('RUB', 'Russian Ruble', '₽');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('UAH', 'Hryvna', '₴');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('AUD', 'Australian Dollar', 'A$');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('KZT', 'Tenge', '₸');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('EUR', 'Euro', '€');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('BRL', 'Brazilian Real', 'R$');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('ARS', 'Argentine Peso', '$A');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('AWG', 'Aruban Florin', '**');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('ZWG', 'Zimbabwe Dollar', '**');


INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (1, 2, 100);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (2, 1, 0.01);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (1, 3, 40);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (2, 3, 0.45);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (4, 1, 0.66);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (1, 4, 1.5);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (1, 6, 0.93);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (7, 1, 0.19);

