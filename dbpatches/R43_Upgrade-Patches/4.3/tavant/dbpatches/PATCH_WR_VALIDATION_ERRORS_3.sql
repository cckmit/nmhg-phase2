--Purpose    : Adding error codes for warranty registration
--Author     : Rahul
--Created On : 26/07/2010

--/* INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'WR060','SERIAL NUMBER')
--/
--INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'warrantyRegistrations'), (SELECT ID FROM UPLOAD_ERROR WHERE CODE = 'WR060'))
--/
--INSERT INTO I18NUPLOAD_ERROR_TEXT VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Unit has already been registered', (SELECT ID FROM UPLOAD_ERROR WHERE CODE = 'WR060'))
--/
commit
/