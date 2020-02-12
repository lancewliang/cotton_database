DROP TABLE IF EXISTS ImportTypeMonth;
 
CREATE TABLE ImportTypeMonth( 
  reportDate  INTEGER       NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  toCountry   CHAR(15)      NOT NULL  ,
  type        CHAR(15)      NOT NULL  ,
  value       FLOAT         NULL  ,
  weightUnit  CHAR(25)      NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , toCountry , type , source)

)ENGINE=InnoDB;
