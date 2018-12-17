--Purpose    : BU Config to display maximum number of records for Multi Claim Maintenance.
--Created On : 24-Aug-2010
--Created By : Saya Sudha

INSERT INTO CONFIG_PARAM values (config_param_seq.nextval,'Number of records allowed for Multi Claim Maintenance','Maximum Number of records for Multi Claim Maintenance',
'maxClaimsAllowedForMultiClaimMaintenance','number',sysdate,null,
sysdate,null,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,null,null,1,null,1,1)
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,500,
(select id from CONFIG_PARAM where NAME='maxClaimsAllowedForMultiClaimMaintenance'),sysdate,
null,sysdate,null,CAST(sysdate AS TIMESTAMP),
CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',null)
/
Commit
/