-- Purpose    : Updated the procedure REMOVE_CAMPAIGN_NOTIFICATION which was not working as expected
-- Author     : Jhulfikar Ali. A
-- Created On : 26-Feb-09

create or replace PROCEDURE                  REMOVE_CAMPAIGN_NOTIFICATION (v_campaign_id NUMBER)
AS
   CURSOR c1
   IS
      SELECT DISTINCT ITEM
                 FROM CAMPAIGN_NOTIFICATION cam_notification
                 WHERE cam_notification.ITEM NOT IN (SELECT ITEMS FROM CAMPAIGN_COVERAGE_ITEMS camcovitems, CAMPAIGN cam
				   WHERE camcovitems.CAMPAIGN_COVERAGE = cam.CAMPAIGN_COVERAGE
                  AND cam.ID = v_campaign_id)
				  AND  cam_notification.NOTIFICATION_STATUS='PENDING'
				  AND cam_notification.campaign = v_campaign_id
				  AND cam_notification.CLAIM IS NULL;

BEGIN
   FOR c1_rec IN c1
   LOOP
      BEGIN
               DELETE FROM CAMPAIGN_NOTIFICATION
                         WHERE CAMPAIGN = v_campaign_id
			               AND ITEM = c1_rec.ITEM;
      END;

      COMMIT;
   END LOOP;
END;
/
COMMIT
/