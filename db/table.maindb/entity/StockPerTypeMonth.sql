DROP TABLE IF EXISTS StockPerTypeMonth;
 
CREATE TABLE StockPerTypeMonth( 
  reportDate  INTEGER       NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  stockType   CHAR(25)      NOT NULL  ,
  value       FLOAT         NOT NULL  ,
  weightUnit  CHAR(25)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , stockType , source)

)ENGINE=InnoDB;
