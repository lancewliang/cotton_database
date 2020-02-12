DROP TABLE IF EXISTS IndustrialStockDayMonth;
 
CREATE TABLE IndustrialStockDayMonth( 
  reportDate  INTEGER       NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  days        FLOAT         NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , commodity , source)

)ENGINE=InnoDB;
