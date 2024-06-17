create table Currencies (
                            ID integer primary key autoincrement,
                            Code varchar,
                            FullName varchar,
                            Sign varchar
);
create unique index idx_code on Currencies (Code);


create table ExchangeRates (
                               ID integer primary key autoincrement,
                               BaseCurrencyId integer,
                               TargetCurrencyId integer,
                               Rate decimal(6)
);
create unique index idx_base_target on ExchangeRates (BaseCurrencyId, TargetCurrencyId);


