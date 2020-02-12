DROP TABLE IF EXISTS RestaurantRecord;
 
CREATE TABLE RestaurantRecord( 
  keyID                 CHAR(60)  NOT NULL  ,
  source                CHAR(10)  NOT NULL  ,
  reportDate            INTEGER   NOT NULL  ,
  recent_order_num      INTEGER   NOT NULL  ,
  month_sales           INTEGER   NOT NULL  ,
  minimum_order_amount  INTEGER   NOT NULL  ,
  rating_count          INTEGER   NOT NULL  ,
  delivery_fee          INTEGER   NOT NULL  ,
  PRIMARY KEY (keyID , source , reportDate)

)ENGINE=InnoDB;
