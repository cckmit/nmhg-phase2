--Patch for Assigning all dealers dealer warranty admin and dealer sales admin roles
--author: ashish.agarwal
--Date: 01/04/2009
CREATE OR REPLACE PROCEDURE ASSIGN_DEALER_RULE IS

  CURSOR ALL_DEALER_USER is select distinct org_user 
  from user_roles where roles in (select id from role where name='dealer');
  
  CURSOR ALL_ROLES_ASSIGNED(org_id number) 
  is select user_roles.ROLES from user_roles where user_roles.ORG_USER=org_id;
  
  sales_role number;
  warranty_role number;
  is_sales_role BOOLEAN:=false;
  is_warranty_role boolean:=false;
  BEGIN
    select id into sales_role from role where name='dealerSalesAdministration';
    select id into warranty_role from role where name='dealerWarrantyAdmin';
    FOR DEALER_USER IN ALL_DEALER_USER
    LOOP 
       is_sales_role:=false;
       is_warranty_role:=false;
       FOR ROLE_ASSIGNED IN ALL_ROLES_ASSIGNED(DEALER_USER.ORG_USER)
       LOOP 
           IF(ROLE_ASSIGNED.roles =warranty_role) THEN 
               is_warranty_role:=true; 
           ELSIF(ROLE_ASSIGNED.roles =sales_role) THEN
               is_sales_role:=true; 
           END IF;
       END LOOP;
       
       IF(is_sales_role=false)
       THEN 
        insert into user_roles values (DEALER_USER.ORG_USER,sales_role);
       END IF;
       
       IF(is_warranty_role=false)
       THEN 
        insert into user_roles values (DEALER_USER.ORG_USER,warranty_role);
       END IF;
       
       COMMIT;
    END LOOP;
    
    EXCEPTION WHEN OTHERS THEN 
    IF ALL_DEALER_USER%ISOPEN 
    THEN 
        CLOSE ALL_DEALER_USER;
    END IF;
    
    IF ALL_ROLES_ASSIGNED%ISOPEN
    THEN
        CLOSE ALL_ROLES_ASSIGNED;
    END IF; 
    
  END;
/
BEGIN
ASSIGN_DEALER_RULE();
END;
/
COMMIT
/