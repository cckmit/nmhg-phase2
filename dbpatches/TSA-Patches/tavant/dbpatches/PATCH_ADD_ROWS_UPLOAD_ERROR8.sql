--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 02/08/2010
--Impact     : None

insert into upload_error values(2378,'DC_78','Replaced IR Parts Serial Num')
/
insert into upload_error values(2361,'DC_79','Replaced IR Parts Quantity')
/
insert into upload_error values(2362,'DC_80','Installed IR Parts Quantity')
/
insert into upload_error values(2363,'DC_81','Replaced IR Parts Serial Num')
/
insert into upload_error values(2364,'DC_82','Replaced IR Parts Serial Num')
/
insert into upload_error values(2365,'DC_83','Replaced IR Parts Quantity')
/
insert into upload_error values(2366,'DC_84','Replaced IR Parts Serial Num')
/
insert into upload_error values(2367,'DC_85','Replaced IR Parts')
/
insert into upload_error values(2368,'DC_86','Replaced IR Parts Quantity')
/
insert into upload_error values(2369,'DC_87','Installed IR Parts Serial Num')
/
insert into upload_error values(2370,'DC_88','Replaced IR Parts')
/
insert into upload_error values(2371,'DC_89','Replaced IR Parts Quantity')
/
insert into upload_error values(2372,'DC_90','Serial Number')
/
insert into upload_error values(2373,'DC_91','Model Number')
/
insert into upload_error values(2374,'DC_92','Competitor Model')
/
insert into upload_error values(2375,'DC_93','Serial Number')
/
insert into upload_error values(2376,'DC_94','Model Number')
/
insert into upload_error values(2377,'DC_95','Competitor Model')
/
insert into upload_error values(2379,'DC_96','Replaced IR Parts')
/
insert into upload_error values(2380,'DC_97','Installed IR Parts')
/
insert into upload_mgt_upload_errors values(4,2379)
/
insert into upload_mgt_upload_errors values(4,2380)
/
insert into upload_mgt_upload_errors values(4,2378)
/
insert into upload_mgt_upload_errors values(4,2361)
/
insert into upload_mgt_upload_errors values(4,2362)
/
insert into upload_mgt_upload_errors values(4,2363)
/
insert into upload_mgt_upload_errors values(4,2364)
/
insert into upload_mgt_upload_errors values(4,2365)
/
insert into upload_mgt_upload_errors values(4,2366)
/
insert into upload_mgt_upload_errors values(4,2367)
/
insert into upload_mgt_upload_errors values(4,2368)
/
insert into upload_mgt_upload_errors values(4,2369)
/
insert into upload_mgt_upload_errors values(4,2370)
/
insert into upload_mgt_upload_errors values(4,2371)
/
insert into upload_mgt_upload_errors values(4,2372)
/
insert into upload_mgt_upload_errors values(4,2373)
/
insert into upload_mgt_upload_errors values(4,2374)
/
insert into upload_mgt_upload_errors values(4,2375)
/
insert into upload_mgt_upload_errors values(4,2376)
/
insert into upload_mgt_upload_errors values(4,2377)
/
insert into i18nupload_error_text values(2378,'en_US','This serialized part is not installed on this Machine. Please provide correct Machine serial number',2378)
/
insert into i18nupload_error_text values(2361,'en_US','Serialized replaced part quatity should be one only',2361)
/
insert into i18nupload_error_text values(2362,'en_US','Serialized installed part quatity should be one only',2362)
/
insert into i18nupload_error_text values(2363,'en_US','Please provide same serialized replaced part',2363)
/
insert into i18nupload_error_text values(2364,'en_US','Please remove serialized  replaced part',2364)
/
insert into i18nupload_error_text values(2365,'en_US','Replaced part quatity should be one only',2365)
/
insert into i18nupload_error_text values(2366,'en_US','Please remove serialized part',2366)
/
insert into i18nupload_error_text values(2367,'en_US','Please add correct part',2367)
/
insert into i18nupload_error_text values(2368,'en_US','Replaced part quatity should be one only',2368)
/
insert into i18nupload_error_text values(2369,'en_US','Please add non serialized part only',2369)
/
insert into i18nupload_error_text values(2370,'en_US','Please add correct replaced part',2370)
/
insert into i18nupload_error_text values(2371,'en_US','Replaced part quatity should be one only',2371)
/
insert into i18nupload_error_text values(2372,'en_US','This Part is already installed on other Unit. Please file claim on same serialized host machine',2372)
/
insert into i18nupload_error_text values(2373,'en_US','This Part is already installed on Unit. Please file claim as Part installed on serialized host machine',2373)
/
insert into i18nupload_error_text values(2374,'en_US','This Part is already installed on Unit.  Please file claim as Part installed on serialized host machine',2374)
/
insert into i18nupload_error_text values(2375,'en_US','This Part is not installed on Serialized Unit. Please file claim as "Part installed on competitor model" or "Part installed on non-serialized host machine"',2375)
/
insert into i18nupload_error_text values(2376,'en_US','This part is not installed on Non Serialized Unit. Please file a claim as Part not installed',2376)
/
insert into i18nupload_error_text values(2377,'en_US','This part is not installed on Competitor Model. Please file a claim as Part not installed',2377)
/
insert into i18nupload_error_text values(2379,'en_US','Please add replaced part one only',2379)
/
insert into i18nupload_error_text values(2380,'en_US','Please add installed part one only',2380)
/
commit
/