
	DROP INDEX YieldYear_pk ON YieldYear;

   CREATE   INDEX YieldYear_pk ON  YieldYear (
     year,
commodity,
country,
source);
	
	