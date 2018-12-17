-- Patch to insert a new role
-- Author: Deepak
-- Created On : 26-SEP-2013

insert into role values(ROLE_SEQ.nextval, 'masterSupplier', 1, sysdate, 'master supplier  role', sysdate, null, sysdate, sysdate, 1 , 'master Supplier','INTERNAL','Master Supplier')
/
insert into user_roles values( (select id from org_user where login='eipaters_supplier'),( select id from role where name='masterSupplier'))
/
commit
/

