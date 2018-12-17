--PURPOSE    : patch to add technician field in service table
--AUTHOR     : Raghu
--CREATED ON : 10-FEB-2014

alter table service add (service_technician varchar2(2000))
/