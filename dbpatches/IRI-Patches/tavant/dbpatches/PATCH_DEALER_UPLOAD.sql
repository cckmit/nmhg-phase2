-- Purpose: For allowing the Dealer for Customer and Draft Claim upload
-- Author: Jhulfikar Ali A
-- Created On: 16 Mar 2009

insert into upload_roles values((select id from upload_mgt where name_of_template='draftWarrantyClaims'), (select id from role where name='dealer'))
/
insert into upload_roles values((select id from upload_mgt where name_of_template='customerUpload'), (select id from role where name='dealer'))
/
update upload_mgt 
set template_path = '.\pages\secure\admin\upload\templates\Template-DraftClaimUpload.xls'
where name_of_template='draftWarrantyClaims'
/
update upload_mgt 
set template_path = '.\pages\secure\admin\upload\templates\Template-PartSourceHistory.xls'
where name_of_template='partSourceHistory'
/
COMMIT
/