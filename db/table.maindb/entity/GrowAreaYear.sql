DROP TABLE IF EXISTS GrowAreaYear;
 
CREATE TABLE GrowAreaYear( 
  year          CHAR(15)      NOT NULL  ,
  reportDate    INTEGER       NOT NULL  ,
  country       CHAR(15)      NOT NULL  ,
  reportStatus  CHAR(1)       NOT NULL  ,
  value         FLOAT         NOT NULL  ,
  areaUnit      CHAR(25)      NOT NULL  ,
  commodity     CHAR(15)      NOT NULL  ,
  source        VARCHAR(25)   NOT NULL  ,
  comment       VARCHAR(255)  NULL  ,
  updatedBy     CHAR(10)      NULL  ,
  updatedAt     DATETIME          NOT NULL  ,
  PRIMARY KEY (year , reportDate , country , reportStatus , commodity , source)

)ENGINE=InnoDB;
