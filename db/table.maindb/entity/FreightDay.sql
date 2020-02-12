DROP TABLE IF EXISTS FreightDay;
 
CREATE TABLE FreightDay( 
  reportDate  INTEGER       NOT NULL  ,
  from        CHAR(15)      NOT NULL  ,
  to          CHAR(15)      NOT NULL  ,
  value1      FLOAT         NOT NULL  ,
  value2      FLOAT         NOT NULL  ,
  priceUnit   CHAR(20)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , from , to , source)

)ENGINE=InnoDB;
