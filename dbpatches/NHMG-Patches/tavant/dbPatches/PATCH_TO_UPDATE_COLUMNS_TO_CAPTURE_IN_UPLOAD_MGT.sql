--Purpose    : Patch for updating columns_to_capture 30 to 32 in UPLOAD_MGT
--Author     : Jyoti Chauhan	
--Created On : 18-MAR-2013

update upload_mgt set columns_to_capture=32 where columns_to_capture=30
/
commit
/