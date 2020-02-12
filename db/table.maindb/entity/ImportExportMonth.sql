DROP TABLE IF EXISTS ImportExportMonth;
 
CREATE TABLE ImportExportMonth( 
  reportDate   INTEGER       NOT NULL  ,
  toCountry    CHAR(15)      NOT NULL  ,
  fromCountry  CHAR(15)      NOT NULL  ,
  value        FLOAT         NULL  ,
  weightUnit   CHAR(25)      NULL  ,
  commodity    CHAR(15)      NOT NULL  ,
  source       VARCHAR(25)   NOT NULL  ,
  comment      VARCHAR(255)  NULL  ,
  updatedBy    CHAR(10)      NULL  ,
  updatedAt    DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , toCountry , fromCountry , commodity , source)

)ENGINE=InnoDB;
