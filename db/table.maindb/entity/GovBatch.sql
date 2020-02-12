DROP TABLE IF EXISTS GovBatch;
 
CREATE TABLE GovBatch( 
  name          CHAR(25)      NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  country       CHAR(15)      NOT NULL  ,
  startDate     INTEGER       NOT NULL  ,
  endDate       INTEGER       NOT NULL  ,
  buyValue      FLOAT         NULL  ,
  sellValue     FLOAT         NULL  ,
  reserveValue  FLOAT         NULL  ,
  weightUnit    CHAR(25)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (name , commodity , country , source)

)ENGINE=InnoDB;
