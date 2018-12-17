--Purpose    : Adding error codes for requests for extension upload, changed as a part of 4.3 upgrade
--Author     : kuldeep.patil
--Created On : 12-Oct-2010

INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE001','DEALER NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE002','DEALER NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE003','SERIAL NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE004','SERIAL NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE005','ITEM NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE006','ITEM NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE007','DELIERY DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE008','POLICY CODE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE009','POLICY CODE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE010','POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE011','GOODWILL POLICY CODE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE012','GOODWILL POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE013','DELIERY DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE014','DELIVERY DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE015','BUSINESS UNIT INFO')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE016','BUSINESS UNIT INFO')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE017','POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE018','POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE019','SERIAL NUMBER')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE020','GOODWILL POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE021','ACTION PERFORMED')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE022','GOODWILL POLICY END DATE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE023','GOODWILL POLICY CODE')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE024','ACTION PERFORMED')
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE025','ACTION PERFORMED')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE001'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE002'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE003'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE004'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE005'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE006'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE007'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE008'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE009'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), (SELECT ID FROM UPLOAD_ERROR where code = 'RE010'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE011'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE012'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE013'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE014'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE015'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE016'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE017'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE018'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE019'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE020'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE021'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE022'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE023'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE024'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE025'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Dealer Number is null', (select id from UPLOAD_ERROR where code = 'RE001'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Dealer Number is invalid', (select id from UPLOAD_ERROR where code = 'RE002'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Serial number is null', (select id from UPLOAD_ERROR where code = 'RE003'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Serial number is invalid', (select id from UPLOAD_ERROR where code = 'RE004'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Item number is null', (select id from UPLOAD_ERROR where code = 'RE005'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Item number is invalid', (select id from UPLOAD_ERROR where code = 'RE006'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Delivery date is null', (select id from UPLOAD_ERROR where code = 'RE007'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Policy code is null', (select id from UPLOAD_ERROR where code = 'RE008'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Policy code is invalid',(select id from UPLOAD_ERROR where code = 'RE009'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Policy end date is null', (select id from UPLOAD_ERROR where code = 'RE010'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy code is invalid', (select id from UPLOAD_ERROR where code = 'RE011'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy end date is null', (select id from UPLOAD_ERROR where code = 'RE012'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Delivery date is invalid', (select id from UPLOAD_ERROR where code = 'RE013'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Delivery date is not equal to actual delivery date',(select id from UPLOAD_ERROR where code = 'RE014'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Business unit info is null',(select id from UPLOAD_ERROR where code = 'RE015'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Business unit info is invalid',(select id from UPLOAD_ERROR where code = 'RE016'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Policy end date is invalid',(select id from UPLOAD_ERROR where code = 'RE017'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Policy end date is not equal to actual policy end date',(select id from UPLOAD_ERROR where code = 'RE018'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Failed due to errors in other records for this unit', (select id from UPLOAD_ERROR where code = 'RE019'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy end date is invalid', (select id from UPLOAD_ERROR where code = 'RE020'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Action performed is not same on all records for this unit',(select id from UPLOAD_ERROR where code = 'RE021'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy end date is before delivery date', (select id from UPLOAD_ERROR where code = 'RE022'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy code is null on all records for this unit', (select id from UPLOAD_ERROR where code = 'RE023'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Action performed is null', (select id from UPLOAD_ERROR where code = 'RE024'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Action performed is invalid', (select id from UPLOAD_ERROR where code = 'RE025'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE026','DEALER NUMBER')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE026'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Dealer number is not same as requesting dealer number', (select id from UPLOAD_ERROR where code = 'RE026'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE027','COMMENTS')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE027'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Comments are required if request is DENIED or FORWARDED to dealer', (select id from UPLOAD_ERROR where code = 'RE027'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE028','HOURS COVERED')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE028'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Hours covered is null', (select id from UPLOAD_ERROR where code = 'RE028'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE029','HOURS COVERED')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE029'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Hours covered must be a valid number greater than 0', (select id from UPLOAD_ERROR where code = 'RE029'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'RE030','GOODWILL POLICY CODE')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'),(SELECT ID FROM UPLOAD_ERROR where code = 'RE030'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Goodwill policy code is duplicate for the same unit', (select id from UPLOAD_ERROR where code = 'RE030'))
/
commit
/