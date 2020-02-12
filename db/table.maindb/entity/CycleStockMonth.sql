DROP TABLE IF EXISTS CycleStockMonth;
 
CREATE TABLE CycleStockMonth( 
  reportDate      INTEGER       NOT NULL  ,
  commodity       CHAR(15)      NOT NULL  ,
  country         CHAR(15)      NOT NULL  ,
  state           CHAR(25)      NOT NULL  ,
  value           FLOAT         NOT NULL  ,
  predictedValue  FLOAT         NULL  ,
  weightUnit      CHAR(25)      NOT NULL  ,
  source          VARCHAR(25)   NOT NULL  ,
  comment         VARCHAR(255)  NULL  ,
  updatedBy       CHAR(10)      NULL  ,
  updatedAt       DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , state , source)

)ENGINE=InnoDB;
