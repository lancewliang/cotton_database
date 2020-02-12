
	DROP INDEX IndustrialStockMonth_pk ON IndustrialStockMonth;

   CREATE   INDEX IndustrialStockMonth_pk ON  IndustrialStockMonth (
     reportDate,
commodity,
country,
source);
	
	