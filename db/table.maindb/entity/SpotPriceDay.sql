DROP TABLE IF EXISTS SpotPriceDay;
 
CREATE TABLE SpotPriceDay( 
  reportDate  CHAR(25)      NOT NULL  ,
  commodity   CHAR(25)      NOT NULL  ,
  priceType   CHAR(25)      NOT NULL  ,
  value       FLOAT         NULL  ,
  priceUnit   CHAR(20)      NOT NULL  ,
  weightUnit  CHAR(25)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , priceType , source)

)ENGINE=InnoDB;
