
	DROP INDEX FreightDay_pk ON FreightDay;

   CREATE   INDEX FreightDay_pk ON  FreightDay (
     reportDate,
from,
to,
source);
	
	