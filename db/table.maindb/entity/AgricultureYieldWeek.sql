DROP TABLE IF EXISTS AgricultureYieldWeek;
 
CREATE TABLE AgricultureYieldWeek( 
  reportDate        INTEGER       NOT NULL  ,
  commodity         CHAR(15)      NOT NULL  ,
  country           CHAR(15)      NOT NULL  ,
  pickingRate       FLOAT         NOT NULL  ,
  sellRate          FLOAT         NOT NULL  ,
  workingRate       FLOAT         NOT NULL  ,
  salesRate         FLOAT         NOT NULL  ,
  salesProcessRate  FLOAT         NOT NULL  ,
  source            VARCHAR(25)   NOT NULL  ,
  comment           VARCHAR(255)  NULL  ,
  updatedBy         CHAR(10)      NULL  ,
  updatedAt         DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , commodity , country , source)

)ENGINE=InnoDB;
