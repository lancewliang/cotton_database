
	DROP INDEX ConsumptionYear_pk ON ConsumptionYear;

   CREATE   INDEX ConsumptionYear_pk ON  ConsumptionYear (
     year,
commodity,
country,
source);
	
	