--Purpose    : Adding error codes for warranty claim upload,changes made as a part of 4.3 upgrade
--Author     : Bharath kumar
--Created On : 02/08/2010
--Impact     : None

--insert into upload_error values(upload_error_seq.nextval,'DC_78','Replaced IR Parts Serial Num')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_79','Replaced IR Parts Quantity')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_80','Installed IR Parts Quantity')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_81','Replaced IR Parts Serial Num')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_82','Replaced IR Parts Serial Num')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_83','Replaced IR Parts Quantity')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_84','Replaced IR Parts Serial Num')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_85','Replaced IR Parts')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_86','Replaced IR Parts Quantity')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_87','Installed IR Parts Serial Num')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_88','Replaced IR Parts')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_89','Replaced IR Parts Quantity')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_90','Serial Number')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_91','Model Number')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_92','Competitor Model')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_93','Serial Number')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_94','Model Number')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_95','Competitor Model')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_96','Replaced IR Parts')
--/
--insert into upload_error values(upload_error_seq.nextval,'DC_97','Installed IR Parts')
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_96'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_97'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_78'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_79'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_80'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_81'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_82'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_83'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_84'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_85'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_86'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_87'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_88'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_89'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_90'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_91'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_92'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_93'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_94'))
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC_95'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This serialized part is not installed on this Machine. Please provide correct Machine serial number',(select id from upload_error where code = 'DC_78'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Serialized replaced part quatity should be one only',(select id from upload_error where code = 'DC_79'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Serialized installed part quatity should be one only',(select id from upload_error where code = 'DC_80'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please provide same serialized replaced part',(select id from upload_error where code = 'DC_81'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please remove serialized  replaced part',(select id from upload_error where code = 'DC_82'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Replaced part quatity should be one only',(select id from upload_error where code = 'DC_83'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please remove serialized part',(select id from upload_error where code = 'DC_84'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add correct part',(select id from upload_error where code = 'DC_85'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Replaced part quatity should be one only',(select id from upload_error where code = 'DC_86'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add non serialized part only',(select id from upload_error where code = 'DC_87'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add correct replaced part',(select id from upload_error where code = 'DC_88'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Replaced part quatity should be one only',(select id from upload_error where code = 'DC_89'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This Part is already installed on other Unit. Please file claim on same serialized host machine',(select id from upload_error where code = 'DC_90'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This Part is already installed on Unit. Please file claim as Part installed on serialized host machine',(select id from upload_error where code = 'DC_91'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This Part is already installed on Unit.  Please file claim as Part installed on serialized host machine',(select id from upload_error where code = 'DC_92'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This Part is not installed on Serialized Unit. Please file claim as "Part installed on competitor model" or "Part installed on non-serialized host machine"',(select id from upload_error where code = 'DC_93'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This part is not installed on Non Serialized Unit. Please file a claim as Part not installed',(select id from upload_error where code = 'DC_94'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','This part is not installed on Competitor Model. Please file a claim as Part not installed',(select id from upload_error where code = 'DC_95'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add replaced part one only',(select id from upload_error where code = 'DC_96'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add installed part one only',(select id from upload_error where code = 'DC_97'))
--/
--Kuldeep - Merged with PATCH_ADD_ROWS_UPLOAD_ERROR.sql DB patch
commit
/