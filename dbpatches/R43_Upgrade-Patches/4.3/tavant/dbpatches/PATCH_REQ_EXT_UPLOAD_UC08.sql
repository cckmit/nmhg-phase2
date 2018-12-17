--Purpose    : Adding upload mgt entries for Requests for extension, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None


INSERT INTO UPLOAD_MGT(ID, NAME_OF_TEMPLATE, NAME_TO_DISPLAY, DESCRIPTION, TEMPLATE_PATH, STAGING_TABLE, STAGING_PROCEDURE, 
	VALIDATION_PROCEDURE, UPLOAD_PROCEDURE, COLUMNS_TO_CAPTURE, POPULATION_PROCEDURE, CONSUME_ROWS_FROM, HEADER_ROW_TO_CAPTURE, BACKUP_TABLE)
VALUES(UPLOAD_MGT_SEQ.NEXTVAL, 'requestForExtension', 'Requests for Extension', 'Requests for Extension', './pages/secure/admin/upload/templates/Template-RequestForExtension.xls',
	'STG_REQUESTS_FOR_EXTENSION', NULL, 'UPLOAD_REQ_FOR_EXTN_VALIDATION', 'UPLOAD_REQ_FOR_EXTN_UPLOAD', 13, NULL, 6, 1, NULL)
/
INSERT INTO UPLOAD_ROLES(UPLOAD_MGT, ROLES) 
  VALUES ((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'requestForExtension'), 
	(SELECT ID FROM ROLE WHERE NAME = 'reducedCoverageRequestsApprover'))
/
COMMIT
/