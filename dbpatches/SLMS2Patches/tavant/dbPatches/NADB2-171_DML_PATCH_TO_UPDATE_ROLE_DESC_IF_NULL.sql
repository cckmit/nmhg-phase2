--PURPOSE    : Patch to update role  description if null
--AUTHOR     : Raghu
--CREATED ON : 09-June-2014


update role set description=name where description is null
/
commit
/