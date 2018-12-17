--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 14/04/2010
--Impact     : None

insert into upload_error values(1101,'DC067_IAC',	'ALARM CODES')
/
insert into upload_error values(1102,'DC068_CP','COMMERCIAL POLICY')
/
insert into upload_error values(1103,'DC069_IPI','IS PARAT INSTALLED')
/
insert into upload_error values(1104,'DC070_PITKTSA','IS PARAT INSTALLED ON TKTSA')
/
insert into upload_error values(1105,'DC072_PS','PART SERIAL NUMBER')
/
insert into upload_error values(1106,'DC073_PS','PART SERIAL NUMBER')
/
insert into upload_error values(1107,'DC071_CM',	'COMPETITOR MODEL')
/
insert into i18nupload_error_text values(1101,'en_US','One or more Alarm codes are not valid',1101)
/
insert into i18nupload_error_text values(1102,'en_US','Commercial policy value is not valid',1102)
/
insert into i18nupload_error_text values(1103,'en_US','Is Part Installed value is not valid',1103)
/
insert into i18nupload_error_text values(1104,'en_US','Is Part Installed on TKTSA value is not valid',1104)
/
insert into i18nupload_error_text values(1105,'en_US','Part serial number is null',1105)
/
insert into i18nupload_error_text values(1106,'en_US','Part serail number is not valid',1106)
/
insert into i18nupload_error_text values(1107,'en_US','Competitor model value is not valid',1107)
/
insert into upload_mgt_upload_errors values(4,1101)
/
insert into upload_mgt_upload_errors values(4,1102)
/
insert into upload_mgt_upload_errors values(4,1103)
/
insert into upload_mgt_upload_errors values(4,1104)
/
insert into upload_mgt_upload_errors values(4,1105)
/
insert into upload_mgt_upload_errors values(4,1106)
/
insert into upload_mgt_upload_errors values(4,1107)
/
commit
/


