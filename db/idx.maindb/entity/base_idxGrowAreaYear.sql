
	DROP INDEX GrowAreaYear_pk ON GrowAreaYear;

   CREATE   INDEX GrowAreaYear_pk ON  GrowAreaYear (
     year,
commodity,
country,
source);
	
	