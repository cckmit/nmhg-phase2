--Purpose    : Patch for updating columns_to_capture 33 to 26 in UPLOAD_MGT
--Author     : Jyoti Chauhan	
--Created On : 14-APR-2013

update upload_mgt set columns_to_capture=26 where columns_to_capture=33
/
commit
/