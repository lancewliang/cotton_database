
	DROP INDEX AgricultureYieldWeek_pk ON AgricultureYieldWeek;

   CREATE   INDEX AgricultureYieldWeek_pk ON  AgricultureYieldWeek (
     reportDate,
commodity,
country,
source);
	
	