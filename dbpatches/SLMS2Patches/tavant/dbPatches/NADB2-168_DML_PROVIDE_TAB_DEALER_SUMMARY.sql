--PURPOSE    : Providing tab to dealer summary link.
--AUTHOR     : Chetan
--CREATED ON : 30-MAY-2014
delete from ROLE_PERMISSION_MAPPING where SUBJECT_AREA in (select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation') and FUNCTIONAL_AREA in (select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummaryTab')
/
delete from SUBJECT_FUNC_AREA_MAPPING where FUNCTIONAL_AREA in (select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummaryTab') and SUBJECT_AREA in (select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation')
/
delete from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummaryTab'
/
delete from ROLE_PERMISSION_MAPPING where SUBJECT_AREA in (select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation') and FUNCTIONAL_AREA in (select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary')
/
delete from SUBJECT_FUNC_AREA_MAPPING where FUNCTIONAL_AREA in (select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary') and SUBJECT_AREA in (select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation')
/
delete from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary'
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dealerInformationDealerSummaryTab', 'Dealer Information Tab')
/
INSERT INTO MST_ADMIN_FNC_AREA VALUES  (MST_ADMIN_FNC_AREA_SEQ.nextval, 'dealerInformationDealerSummary', 'Dealer Summary')
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummaryTab'),(select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation'))
/
INSERT INTO SUBJECT_FUNC_AREA_MAPPING VALUES((select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary'),(select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation'))
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummaryTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='dealerInformation'),'dealerInformation:dealerInformationDealerSummaryTab:update' from role where name in ('Viewonly','Internal Read Only','admin','SSDataAdmin')
/
insert into ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.NEXTVAL,id,(select id from MST_ADMIN_FNC_AREA where name='dealerInformationDealerSummary'),(select id from MST_ADMIN_ACTION where ACTION='update'),(select id from MST_ADMIN_SUBJECT_AREA where name='dealerInformation'),'dealerInformation:dealerInformationDealerSummary:update' from role where name in ('Viewonly','Internal Read Only','admin','SSDataAdmin')
/
commit
/