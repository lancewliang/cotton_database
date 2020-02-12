DROP TABLE IF EXISTS SaleMonth;
 
CREATE TABLE SaleMonth( 
  reportDate  INTEGER       NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  value       FLOAT         NULL  ,
  weightUnit  CHAR(15)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , source)

)ENGINE=InnoDB;
