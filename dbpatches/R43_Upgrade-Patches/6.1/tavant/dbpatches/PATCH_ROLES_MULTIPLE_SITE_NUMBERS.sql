--Purpose    : Asoociating roles with upload, changes made as a part of 4.3 upgrade 
--Author     : Surendra Varma
--Created On : 10-june-2011

INSERT INTO UPLOAD_ROLES VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'multipleSiteNumbers'), (select id from role where name = 'admin'))
/
COMMIT
/