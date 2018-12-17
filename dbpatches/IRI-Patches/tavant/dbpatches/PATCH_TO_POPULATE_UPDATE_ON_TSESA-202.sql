ALTER TABLE REQUEST_WNTY_CVG ADD (UPDATED_ON_DATE DATE)
/
CREATE OR REPLACE PROCEDURE UPDATED_DT_WNTY_EXT_REQS
AS

CURSOR C1 IS 
select * from REQUEST_WNTY_CVG  where UPDATED_ON_DATE is null ; 

v_flag number := 0 ;
v_updated_date	date;

BEGIN

FOR C1_REC IN C1 LOOP 

v_flag := v_flag + 1 ;
 
select max(d_updated_on) into v_updated_date 
from REQUEST_WNTY_CVG_AUDIT where REQUEST_WNTY_CVG = c1_rec.id ;

update  REQUEST_WNTY_CVG  set UPDATED_ON_DATE = v_updated_date 
where id =  c1_rec.id ;

if (v_flag = 1000)
then

commit ;
v_flag := 0; 

end if;

END LOOP;
COMMIT;

END;
/
BEGIN 
UPDATED_DT_WNTY_EXT_REQS();
END;
/