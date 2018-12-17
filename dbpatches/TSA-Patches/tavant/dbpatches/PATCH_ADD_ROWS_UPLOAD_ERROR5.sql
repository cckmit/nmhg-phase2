--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 20/05/2010
--Impact     : None

update upload_error set code ='DC072' where code ='DC074'
/
commit
/