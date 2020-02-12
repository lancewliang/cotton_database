
	DROP INDEX GovBatch_pk ON GovBatch;

   CREATE   INDEX GovBatch_pk ON  GovBatch (
     name,
commodity,
country,
source);
	
	