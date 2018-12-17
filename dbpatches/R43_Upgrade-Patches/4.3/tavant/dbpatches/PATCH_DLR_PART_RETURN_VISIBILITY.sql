--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM FOR PART RETURN VISIBILITY FOR DEALERS, CHANGED AS A PART OF 4.3 UPGRADE
--AUTHOR     : Amritha Krishnamoorthy
--CREATED ON : 21-DEC-09

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Enable Dealers to view Part Returns list', 'Enable Dealers to view Part Returns list','enableDealersToViewPartReturnsList','boolean',SYSDATE, '4.3 Upgrade', SYSDATE, NULL, sysdate, 
    sysdate, 'CLAIMS', 'radio', 1, 'CLAIM_RETURN_PART', 
    1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableDealersToViewPartReturnsList'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='enableDealersToViewPartReturnsList'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableDealersToViewPartReturnsList'),sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Transport Solutions ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableDealersToViewPartReturnsList'),sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Hussmann',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableDealersToViewPartReturnsList'),sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'AIR',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableDealersToViewPartReturnsList'),sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'Clubcar ESA',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableDealersToViewPartReturnsList'),sysdate,'4.3 Upgrade',sysdate,56,sysdate,sysdate,1,'TFM',(select id from CONFIG_PARAM_OPTION where value='false'))
/
COMMIT
/