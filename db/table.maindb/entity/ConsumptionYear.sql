DROP TABLE IF EXISTS ConsumptionYear;
 
CREATE TABLE ConsumptionYear( 
  year        CHAR(15)      NOT NULL  ,
  commodity   CHAR(15)      NOT NULL  ,
  country     CHAR(15)      NOT NULL  ,
  value       FLOAT         NULL  ,
  weightUnit  CHAR(25)      NOT NULL  ,
  source      VARCHAR(25)   NOT NULL  ,
  comment     VARCHAR(255)  NULL  ,
  updatedBy   CHAR(10)      NULL  ,
  updatedAt   DATETIME          NOT NULL  ,
  PRIMARY KEY (year , commodity , country , source)

)ENGINE=InnoDB;
