DROP TABLE IF EXISTS WorldSupplyDemandMonthlyHistory;
 
CREATE TABLE WorldSupplyDemandMonthlyHistory( 
  country       CHAR(15)      NOT NULL  ,
  year          CHAR(15)      NOT NULL  ,
  reportDate    CHAR(15)      NOT NULL  ,
  reportStatus  INTEGER       NOT NULL  ,
  beginStock    FLOAT         NOT NULL  ,
  production    FLOAT         NOT NULL  ,
  imports       FLOAT         NOT NULL  ,
  uses          FLOAT         NOT NULL  ,
  exports       FLOAT         NOT NULL  ,
  loss          FLOAT         NOT NULL  ,
  endStock      FLOAT         NOT NULL  ,
  weightUnit    CHAR(15)      NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (country , year , reportDate , reportStatus , commodity , source)

)ENGINE=InnoDB;
