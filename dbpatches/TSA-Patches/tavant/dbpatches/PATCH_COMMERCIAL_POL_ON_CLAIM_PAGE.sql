--Purpose    : BU Config for displaying commercial policy checkbox visible on New claim screen.
--Created On : 26-Oct-2010
--Created By : Surendra Varma

INSERT INTO CONFIG_PARAM VALUES (config_param_seq.nextval,'Display CP Flag on claim Page 1','Display CP Flag (checkbox) on claim Page 1', 'displayCPFlagOnClaimPgOne','boolean',sysdate,'New config for upgrade',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'radio','CLAIMS',1,'CLAIM_INPUT_PARAMETERS',1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING VALUES (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='displayCPFlagOnClaimPgOne'), (select id from CONFIG_PARAM_OPTION where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING VALUES (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from CONFIG_PARAM where NAME='displayCPFlagOnClaimPgOne'), (select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE VALUES (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='displayCPFlagOnClaimPgOne'),sysdate,'New config for upgrade',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
COMMIT
/