DROP TABLE IF EXISTS ExportTaskStatus;
 
CREATE TABLE ExportTaskStatus( 
  type       CHAR(15)  NOT NULL  ,
  commodity  CHAR(15)  NOT NULL  ,
  format     CHAR(15)  NOT NULL  ,
  date       CHAR(15)  NOT NULL  ,
  status     CHAR(15)  NOT NULL  ,
  PRIMARY KEY (type , commodity , format , date)

)ENGINE=InnoDB;
