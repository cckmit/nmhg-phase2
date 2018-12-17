--PURPOSE    : PATCH FOR UPDATING BAR CODES, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010


UPDATE part_return SET bar_code = null WHERe bar_Code IS NOT null
/
commit
/
ALTER TABLE PART_RETURN
      ADD CONSTRAINT PART_RETURN_UQ UNIQUE(BAR_CODE) 
/
     