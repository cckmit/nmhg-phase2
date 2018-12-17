--PURPOSE    : patch to drop columns for techncian tables
--AUTHOR     : Raghu
--CREATED ON : 27-JAN-2014
--deleting extra columns fromTechnician table
  
alter table technician_certification drop (RENEW_DATE,series,technician)
/
alter table technician drop (CERTIFICATION_FROM_DATE, CERTIFICATION_TO_DATE, DATE_OF_HIRE,DATE_OF_RENEWAL)
/