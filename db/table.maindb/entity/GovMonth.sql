DROP TABLE IF EXISTS GovMonth;
 
CREATE TABLE GovMonth( 
  reportDate    INTEGER       NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  country       CHAR(15)      NOT NULL  ,
  buyValue      FLOAT         NULL  ,
  sellValue     FLOAT         NULL  ,
  reserveValue  FLOAT         NULL  ,
  weightUnit    CHAR(25)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , source)

)ENGINE=InnoDB;
