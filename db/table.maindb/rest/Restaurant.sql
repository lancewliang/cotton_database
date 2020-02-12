DROP TABLE IF EXISTS Restaurant;
 
CREATE TABLE Restaurant( 
  keyID       CHAR(60)   NOT NULL  ,
  source      CHAR(10)   NOT NULL  ,
  latitude    FLOAT      NOT NULL  ,
  longitude   FLOAT      NOT NULL  ,
  name        CHAR(100)  NOT NULL  ,
  adress      CHAR(200)  NOT NULL  ,
  reportDate  INTEGER    NOT NULL  ,
  PRIMARY KEY (keyID , source)

)ENGINE=InnoDB;
