
	DROP INDEX SpotPriceDay_pk ON SpotPriceDay;

   CREATE   INDEX SpotPriceDay_pk ON  SpotPriceDay (
     reportDate,
commodity,
priceType,
source);
	
	