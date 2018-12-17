--Purpose : Fix for twms4.3-704, updated the upload status values of Uploaded & Failed statuses
--Author  : raghuram.d
--Date    : 20/Jan/2010

--Uploaded is changed from 3 to 10
update file_upload_mgt set upload_status=10 where upload_status=3
/
--Failed is changed from 4 to 9
update file_upload_mgt set upload_status=9 where upload_status=4
/
commit
/