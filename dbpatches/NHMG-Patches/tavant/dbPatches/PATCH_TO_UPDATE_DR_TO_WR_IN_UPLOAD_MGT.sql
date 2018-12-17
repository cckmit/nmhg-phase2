--Purpose    : Patch for updating DR to WR in UPLOAD_MGT
--Author     : Jyoti Chauhan	
--Created On : 04-MAR-2013

UPDATE UPLOAD_MGT SET NAME_TO_DISPLAY='Delivery Report', DESCRIPTION='Delivery Report' WHERE NAME_TO_DISPLAY='Warranty Registrations'
/
commit
/