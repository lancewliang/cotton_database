DROP TABLE IF EXISTS ExchangeRateDay;
 
CREATE TABLE ExchangeRateDay( 
  reportDate   INTEGER       NOT NULL  ,
  fromCurreny  CHAR(15)      NOT NULL  ,
  toCurreny    CHAR(15)      NOT NULL  ,
  value        FLOAT         NOT NULL  ,
  source       VARCHAR(25)   NOT NULL  ,
  comment      VARCHAR(255)  NULL  ,
  updatedBy    CHAR(10)      NULL  ,
  updatedAt    DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , fromCurreny , toCurreny , source)

)ENGINE=InnoDB;
