
	DROP INDEX BourseStockWeek_pk ON BourseStockWeek;

   CREATE   INDEX BourseStockWeek_pk ON  BourseStockWeek (
     reportDate,
commodity,
bourse,
source);
	
	