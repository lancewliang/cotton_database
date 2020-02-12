DROP TABLE IF EXISTS FuturePriceDay;
 
CREATE TABLE FuturePriceDay( 
  reportDate    INTEGER       NOT NULL  ,
  country       CHAR(15)      NOT NULL  ,
  contract      CHAR(15)      NOT NULL  ,
  bourse        CHAR(15)      NULL  ,
  openingValue  FLOAT         NULL  ,
  topValue      FLOAT         NULL  ,
  minimumValue  FLOAT         NULL  ,
  closingValue  FLOAT         NULL  ,
  volumes       INTEGER       NULL  ,
  priceUnit     CHAR(20)      NOT NULL  ,
  weightUnit    CHAR(25)      NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , contract , commodity , source)

)ENGINE=InnoDB;
