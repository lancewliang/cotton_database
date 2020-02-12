DROP TABLE IF EXISTS SaleDay;
 
CREATE TABLE SaleDay( 
  reportDate  INTEGER       NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  total       FLOAT         NULL  ,
  weightUnit  CHAR(15)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , source)

)ENGINE=InnoDB;
