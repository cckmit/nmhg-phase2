--Purpose    : Patch for adding coverage end date and hours covered columns to claim table
--Author     : Nandakumar Devi
--Created On : 18-May-2013

ALTER TABLE CLAIM ADD (COVERAGE_END_DATE DATE, HOURS_COVERED NUMBER(19))
/