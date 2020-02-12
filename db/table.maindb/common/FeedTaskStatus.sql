DROP TABLE IF EXISTS FeedTaskStatus;
 
CREATE TABLE FeedTaskStatus( 
  type       CHAR(15)  NOT NULL  ,
  filename   CHAR(35)  NOT NULL  ,
  sheetname  CHAR(35)  NOT NULL  ,
  status     CHAR(15)  NOT NULL  ,
  PRIMARY KEY (type , filename , sheetname)

)ENGINE=InnoDB;
