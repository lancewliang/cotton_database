DROP TABLE IF EXISTS PortPriceDay;
 
CREATE TABLE PortPriceDay( 
  reportDate     CHAR(25)      NOT NULL  ,
  country        CHAR(15)      NOT NULL  ,
  fromCountry    CHAR(15)      NULL  ,
  standard       CHAR(25)      NOT NULL  ,
  term           CHAR(25)      NOT NULL  ,
  portPriceType  CHAR(25)      NOT NULL  ,
  value1         FLOAT         NULL  ,
  priceUnit1     CHAR(20)      NULL  ,
  weightUnit1    CHAR(25)      NULL  ,
  value2         FLOAT         NULL  ,
  priceUnit2     CHAR(20)      NULL  ,
  weightUnit2    CHAR(25)      NULL  ,
  commodity      CHAR(15)      NOT NULL  ,
  source         VARCHAR(25)   NOT NULL  ,
  comment        VARCHAR(255)  NULL  ,
  updatedBy      CHAR(10)      NULL  ,
  updatedAt      DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , fromCountry , standard , term , portPriceType , commodity , source)

)ENGINE=InnoDB;
