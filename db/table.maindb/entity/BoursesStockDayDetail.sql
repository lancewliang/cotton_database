DROP TABLE IF EXISTS BoursesStockDayDetail;
 
CREATE TABLE BoursesStockDayDetail( 
  reportDate      INTEGER       NOT NULL  ,
  country         CHAR(15)      NOT NULL  ,
  bourse          CHAR(15)      NOT NULL  ,
  wHId            CHAR(15)      NOT NULL  ,
  wHName          CHAR(30)      NOT NULL  ,
  annual          CHAR(15)      NOT NULL  ,
  grade           CHAR(15)      NOT NULL  ,
  producingArea   CHAR(15)      NOT NULL  ,
  value           FLOAT         NOT NULL  ,
  predictedValue  FLOAT         NOT NULL  ,
  weightUnit      CHAR(25)      NOT NULL  ,
  commodity       CHAR(15)      NOT NULL  ,
  source          VARCHAR(25)   NOT NULL  ,
  comment         VARCHAR(255)  NULL  ,
  updatedBy       CHAR(10)      NULL  ,
  updatedAt       DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , bourse , wHId , wHName , annual , grade , producingArea , commodity , source)

)ENGINE=InnoDB;
