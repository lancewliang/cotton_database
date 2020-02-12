DROP TABLE IF EXISTS CountryMainIndex;
 
CREATE TABLE CountryMainIndex( 
  reportDate     CHAR(25)      NOT NULL  ,
  reportHour     CHAR(8)       NOT NULL  ,
  source         VARCHAR(25)   NOT NULL  ,
  country        VARCHAR(8)    NOT NULL  ,
  title          CHAR(45)      NOT NULL  ,
  currency       VARCHAR(8)    NOT NULL  ,
  importance     INTEGER       NOT NULL  ,
  forecastValue  CHAR(10)      NULL  ,
  actualValue    CHAR(10)      NULL  ,
  previousValue  CHAR(10)      NULL  ,
  remark         VARCHAR(255)  NULL  ,
  mark           VARCHAR(255)  NULL  ,
  description    VARCHAR(255)  NULL  ,
  comment        VARCHAR(255)  NULL  ,
  updatedBy      CHAR(10)      NULL  ,
  updatedAt      DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , reportHour , source , country , title)

)ENGINE=InnoDB;
