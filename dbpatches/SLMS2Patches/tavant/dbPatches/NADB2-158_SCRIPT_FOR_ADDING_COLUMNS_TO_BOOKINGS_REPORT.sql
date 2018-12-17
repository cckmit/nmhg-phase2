--Purpose    :  patch to add columns in bookings_report
--Author     : kalyani
--Created On : 22-May-2014

alter table bookings_report add WARRANTY_LAST_PROCESSED_TIME  TIMESTAMP(6)
/
alter table bookings_report add FAILED_BOOKING_IDS  VARCHAR2(4000 CHAR)
/
alter table bookings_report RENAME COLUMN REPORTING_TIME   to INV_TRANS_LAST_PROCESSED_TIME
/