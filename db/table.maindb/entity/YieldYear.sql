DROP TABLE IF EXISTS YieldYear;
 
CREATE TABLE YieldYear( 
  year          CHAR(15)      NOT NULL  ,
  reportDate    INTEGER       NOT NULL  ,
  country       CHAR(15)      NOT NULL  ,
  value         FLOAT         NOT NULL  ,
  weightUnit    CHAR(25)      NOT NULL  ,
  reportStatus  CHAR(1)       NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (year , reportDate , country , reportStatus , commodity , source)

)ENGINE=InnoDB;
