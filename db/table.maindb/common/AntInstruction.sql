DROP TABLE IF EXISTS AntInstruction;
 
CREATE TABLE AntInstruction( 
  name      CHAR(35)    NOT NULL  ,
  status    CHAR(15)    NOT NULL  ,
  log       CHAR(255)  NOT NULL  ,
  updateAt  DATETIME        NOT NULL  ,
  PRIMARY KEY (name)

)ENGINE=InnoDB;
