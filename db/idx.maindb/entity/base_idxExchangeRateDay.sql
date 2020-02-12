
	DROP INDEX ExchangeRateDay_pk ON ExchangeRateDay;

   CREATE   INDEX ExchangeRateDay_pk ON  ExchangeRateDay (
     reportDate,
from,
to,
source);
	
	