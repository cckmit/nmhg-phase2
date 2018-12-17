--Purpose    : Asoociating roles with upload, changes made as a part of 4.3 upgrade 
--Author     : Kuldeep Patil
--Created On : 5-Oct-2010

INSERT INTO UPLOAD_ROLES VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'), (select id from role where name = 'dealerWarrantyAdmin'))
/
COMMIT
/