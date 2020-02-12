DROP TABLE IF EXISTS GovDay;
 
CREATE TABLE GovDay( 
  reportDate      INTEGER       NOT NULL  ,
  country         CHAR(15)      NOT NULL  ,
  buyValue        FLOAT         NULL  ,
  sellValue       FLOAT         NULL  ,
  totalBuyValue   FLOAT         NULL  ,
  totalSellValue  FLOAT         NULL  ,
  weightUnit      CHAR(25)      NOT NULL  ,
  commodity       CHAR(15)      NOT NULL  ,
  source          VARCHAR(25)   NOT NULL  ,
  comment         VARCHAR(255)  NULL  ,
  updatedBy       CHAR(10)      NULL  ,
  updatedAt       DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , country , commodity , source)

)ENGINE=InnoDB;
