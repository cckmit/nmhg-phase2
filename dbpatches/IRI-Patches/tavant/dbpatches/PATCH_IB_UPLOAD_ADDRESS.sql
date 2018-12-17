--Purpose : renamed address to address line1, added line2 & line 3 in the IB upload template
--Author : raghuram.d
--Date : 22-Jul-09

alter table stg_install_base rename column address to address_line1
/
update upload_mgt set columns_to_capture=37 where name_of_template='installBaseUpload'
/
commit
/