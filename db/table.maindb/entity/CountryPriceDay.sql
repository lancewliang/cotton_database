DROP TABLE IF EXISTS CountryPriceDay;
 
CREATE TABLE CountryPriceDay( 
  reportDate  CHAR(25)      NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  state       CHAR(25)      NOT NULL  ,
  standard    CHAR(25)      NOT NULL  ,
  value       FLOAT         NULL  ,
  priceUnit   CHAR(20)      NOT NULL  ,
  unitType    CHAR(10)      NOT NULL  ,
  unit        CHAR(25)      NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , state , standard , commodity , source)

)ENGINE=InnoDB;
