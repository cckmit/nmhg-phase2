--Purpose :Insert new params and create new node related to partshippednotreceived inbox
--Author     : PRADYOT.ROUT		
--Created On : 17-aUG-2008
--Splitted into two files by priyank.gupta on 24th september as this patch was combination of regular data updated
--and jbpm data update while we wanted only regular data update. So splitting into two files for future easy runs.

--INSERT 2 NEW PARAM CONFIG FOR PARTSHIPPED NOT RECEIVED INBOX 

INSERT INTO CONFIG_PARAM (ID,DESCRIPTION,DISPLAY_NAME,NAME,TYPE) VALUES (CONFIG_PARAM_SEQ.NEXTVAL,'daysForPartsShippedNotReceived','daysForPartsShippedNotReceived','daysForPartsShippedNotReceived','number')
/
INSERT INTO CONFIG_VALUE (ID,ACTIVE,VALUE,CONFIG_PARAM,BUSINESS_UNIT_INFO) VALUES (CONFIG_PARAM_SEQ.NEXTVAL,1,0,(SELECT ID FROM CONFIG_PARAM WHERE NAME='daysForPartsShippedNotReceived'),'Club Car')
/
INSERT INTO CONFIG_PARAM  (ID,DESCRIPTION,DISPLAY_NAME,NAME,TYPE) VALUES(CONFIG_PARAM_SEQ.NEXTVAL,'actionForPartsShippedNotReceived','actionForPartsShippedNotReceived','actionForPartsShippedNotReceived'
      ,'java.lang.String')
/
INSERT INTO CONFIG_VALUE (ID,ACTIVE,VALUE,CONFIG_PARAM,BUSINESS_UNIT_INFO) VALUES (CONFIG_PARAM_SEQ.NEXTVAL ,1,'MoveToShippedNotReceivedInbox',(SELECT ID FROM CONFIG_PARAM WHERE NAME='actionForPartsShippedNotReceived'),
   'Club Car')
/
ALTER TABLE CLAIM ADD PRT_SHP_NTRCVD  NUMBER(1,0)
/
COMMIT
/