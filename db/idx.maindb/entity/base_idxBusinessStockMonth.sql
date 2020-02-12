
	DROP INDEX BusinessStockMonth_pk ON BusinessStockMonth;

   CREATE   INDEX BusinessStockMonth_pk ON  BusinessStockMonth (
     reportDate,
commodity,
country,
state,
source);
	
	