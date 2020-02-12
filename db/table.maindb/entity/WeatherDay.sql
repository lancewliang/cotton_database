DROP TABLE IF EXISTS WeatherDay;
 
CREATE TABLE WeatherDay( 
  reportDate     INTEGER       NOT NULL  ,
  weatherRegion  CHAR(140)      NOT NULL  ,
  high           INTEGER       NOT NULL  ,
  low            INTEGER       NOT NULL  ,
  precip         FLOAT         NOT NULL  ,
  precipUnit     CHAR(10)      NOT NULL  ,
  snow           FLOAT         NOT NULL  ,
  snowUnit       CHAR(10)      NOT NULL  ,
  forecast       CHAR(140)     NOT NULL  ,
  avgHigh        INTEGER       NOT NULL  ,
  avgLow         INTEGER       NOT NULL  ,
  source         VARCHAR(25)   NOT NULL  ,
  comment        VARCHAR(255)  NULL  ,
  updatedBy      CHAR(10)      NULL  ,
  updatedAt      DATETIME          NOT NULL  ,
  PRIMARY KEY (reportDate , weatherRegion , source)

)ENGINE=InnoDB;
