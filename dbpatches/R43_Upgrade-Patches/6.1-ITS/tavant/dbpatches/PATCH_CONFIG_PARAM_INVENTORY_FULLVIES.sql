--Purpose    : Problem in opening Inventory. This is was missed in3.4 upgrade.
--AUTHOR     : Surendra
--CREATED ON : 21-04-2011

INSERT INTO CONFIG_VALUE 
values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='enableInventoryFullView'),
sysdate,'3.4 Upgrade',sysdate,56,sysdate,sysdate,1,'ITS',(select id from CONFIG_PARAM_OPTION where value='false'))
/
Commit
/