--PURPOSE    : PATCH_TO_CREATE_CAMP_NOTIFICATION_ATTACHMENTS_TABLE
--AUTHOR     : RAVIKUMAR.Y
--CREATED ON : 17-AUG-12

CREATE TABLE CAMP_NOTIFICATION_ATTACHMENTS
  (
   CAMPAIGN_NOTIFICATION    NUMBER(19,0) NOT NULL ENABLE,
   ATTACHMENTS NUMBER(19,0) NOT NULL ENABLE
  )
/
ALTER TABLE CAMP_NOTIFICATION_ATTACHMENTS ADD CONSTRAINT CAMPATTACHMENTS_CAMPNOTE_FK FOREIGN KEY
  (
   CAMPAIGN_NOTIFICATION
  )
  REFERENCES CAMPAIGN_NOTIFICATION
  (
   ID
  )
  ENABLE
/
ALTER TABLE CAMP_NOTIFICATION_ATTACHMENTS ADD CONSTRAINT CAMPNOTE_ATTACHMNTS_FK FOREIGN KEY
  (
    ATTACHMENTS
  )
  REFERENCES DOCUMENT
  (
   ID
  )
  ENABLE
/
CREATE INDEX CAMPMOTEATTACHMENTS_CAMPG_IX ON CAMP_NOTIFICATION_ATTACHMENTS
  (
   CAMPAIGN_NOTIFICATION
  )
/