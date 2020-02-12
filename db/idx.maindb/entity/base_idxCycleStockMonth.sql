
	DROP INDEX CycleStockMonth_pk ON CycleStockMonth;

   CREATE   INDEX CycleStockMonth_pk ON  CycleStockMonth (
     reportDate,
commodity,
country,
state,
source);
	
	