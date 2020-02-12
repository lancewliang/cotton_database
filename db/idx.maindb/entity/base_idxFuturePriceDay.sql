
	DROP INDEX FuturePriceDay_pk ON FuturePriceDay;

   CREATE   INDEX FuturePriceDay_pk ON  FuturePriceDay (
     reportDate,
commodity,
country,
bourse,
contract,
source);
	
	