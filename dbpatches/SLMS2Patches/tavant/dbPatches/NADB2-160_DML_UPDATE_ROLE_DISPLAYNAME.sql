--PURPOSE    : Roles Display Name Update, providing permissions of internalUserAdmin to admin & unmapping internalUserAdmin from aaabott, map aaabott_supplier to masterSupplier
--AUTHOR     : Chetan
--CREATED ON : 24-MAY-2014
--Roles Corrections
update role set DISPLAY_NAME='DCAP Admin' where name='dcapAdmin'
/
update role set DISPLAY_NAME='DCAP Dealer' where name='dcapDealer'
/
update role set DISPLAY_NAME='DCAP Claim Reviewer' where name='dcapClaimReviewer'
/
update role set DISPLAY_NAME='Supplier Recovery Claim Initiator' where name='supplierRecoveryInitiator'
/
update role set DISPLAY_NAME='DCAP Regional Director' where name='dcapRegionalDirector'
/
INSERT INTO ROLE_PERMISSION_MAPPING SELECT ROLE_PERMISSION_MAPPING_SEQ.nextval,(select id from role where name='admin'),functional_area,action,subject_area,permission_string
FROM ROLE_PERMISSION_MAPPING
WHERE ROLE_DEF_ID=
  (SELECT id FROM role WHERE name='internalUserAdmin'
  )
AND PERMISSION_STRING NOT LIKE '%:view'
AND PERMISSION_STRING NOT IN
  (SELECT permission_string
  FROM ROLE_PERMISSION_MAPPING
  WHERE role_def_id=
    (SELECT id FROM role WHERE name='admin'
    )
  AND permission_string IN
    (SELECT permission_string
    FROM ROLE_PERMISSION_MAPPING
    WHERE ROLE_DEF_ID=
      (SELECT id FROM role WHERE name='internalUserAdmin'
      )
    AND PERMISSION_STRING NOT LIKE '%:view'
    )
  )
/
delete from user_roles where org_user=(select id from org_user where login='aaabott') and roles=(select id from role where name='internalUserAdmin')
/
insert into USER_ROLES values((select id from ORG_USER where LOGIN='aaabott_supplier'), (select id from role where name='masterSupplier'))
/
commit
/