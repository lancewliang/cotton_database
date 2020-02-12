DROP TABLE IF EXISTS BoursesStockDay;
 
CREATE TABLE BoursesStockDay( 
  reportDate      INTEGER       NOT NULL  ,
  country         CHAR(15)      NOT NULL  ,
  bourse          CHAR(15)      NOT NULL  ,
  value           FLOAT         NOT NULL  ,
  predictedValue  FLOAT         NOT NULL  ,
  weightUnit      CHAR(25)      NOT NULL  ,
  commodity       CHAR(15)      NOT NULL  ,
  source          VARCHAR(25)   NOT NULL  ,
  comment         VARCHAR(255)  NULL  ,
  updatedBy       CHAR(10)      NULL  ,
  updatedAt       DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , bourse , commodity , source)

)ENGINE=InnoDB;
