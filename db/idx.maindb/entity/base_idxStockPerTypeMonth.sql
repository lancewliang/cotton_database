
	DROP INDEX StockPerTypeMonth_pk ON StockPerTypeMonth;

   CREATE   INDEX StockPerTypeMonth_pk ON  StockPerTypeMonth (
     reportDate,
commodity,
country,
type,
source);
	
	