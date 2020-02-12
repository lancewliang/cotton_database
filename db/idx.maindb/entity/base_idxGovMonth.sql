
	DROP INDEX GovMonth_pk ON GovMonth;

   CREATE   INDEX GovMonth_pk ON  GovMonth (
     reportDate,
commodity,
country,
source);
	
	