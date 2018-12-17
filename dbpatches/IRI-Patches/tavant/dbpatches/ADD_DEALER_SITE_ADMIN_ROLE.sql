----For ticket TSESA-774 to add a new role "dealerSiteAdmin"

INSERT INTO ROLE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON,
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, D_ACTIVE,
DISPLAY_NAME ) VALUES ( 
1119889226880, 'dealerSiteAdmin', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL)
 
commit;

---************************************PROC TO ADD THIS ROLE TO DEALERS********************************************

CREATE OR REPLACE PROCEDURE ADD_DEALER_ADMIN_SITE_ROLE 
AS

CURSOR C1 IS
select distinct org_user from user_roles where roles in (1100000000021,28,33) 
and org_user in (
select org_user from bu_user_mapping where bu = 'Hussmann') ;


BEGIN 

FOR C1_REC IN C1 LOOP

insert into user_roles  values ( c1_rec.org_user, 1119889226880) ;

COMMIT;

END LOOP;

COMMIT ;

END;

---**********************************************************************************************************************