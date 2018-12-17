--Purpose    : Problem in Updating Business Conditions. This is was missed in3.4 upgrade.
--AUTHOR     : Surendra
--CREATED ON : 13-07-2011


INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='dealerGroupCode'),
sysdate,'3.4 Upgrade',sysdate,'',sysdate,sysdate,1,'ITS',(select id from CONFIG_PARAM_OPTION where value='false'))
/
INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='useInstalledPartsForRecoveryClaim'),
sysdate,'3.4 Upgrade',sysdate,'',sysdate,sysdate,1,'ITS',(select id from CONFIG_PARAM_OPTION where value='false'))
/
Commit
/