create or replace
PROCEDURE                   "GEN_CAMPAIGN_NOTIFICATION_ALL" (v_campaign_id NUMBER)
AS
   CURSOR c1
   IS
     SELECT DISTINCT csci.items
		FROM campaign_sno_coverage_items csci,
		  campaign c,
		  campaign_coverage cc
		WHERE c.campaign_coverage = cc.id
		and cc.serial_number_coverage = csci.campaign_sno_coverage
		and c.id = v_campaign_id
		union
		SELECT DISTINCT crci.items
		FROM campaign_range_coverage_items crci,
		  campaign c,
		  campaign_coverage cc
		WHERE c.campaign_coverage = cc.id
		and cc.range_coverage = crci.campaign_range_coverage
		and c.id = v_campaign_id;
   v_campaign_coverage_id   NUMBER := 0;
   v_dealership_id          NUMBER := 0;
   v_dealer_id              NUMBER := 0;
   v_seq_id                 NUMBER := 0;
   v_temp_id                NUMBER := 0;
   v_campaign_notification_id   NUMBER := 0;
   v_count                      NUMBER := 0;

BEGIN

   DELETE FROM CAMPAIGN_NOTIFICATION NOTIFICATION
         WHERE NOTIFICATION.CAMPAIGN = v_campaign_id
           AND NOTIFICATION.CLAIM is null
           AND NOTIFICATION.field_mod_inv_status is null
		   AND NOT EXISTS(SELECT FOR_FIELD_MOD FROM FIELD_MOD_UPDATE_AUDIT WHERE FOR_FIELD_MOD=NOTIFICATION.ID)
           AND NVL (NOTIFICATION.notification_status, 'PENDING') = 'PENDING';

   COMMIT;

   BEGIN
      SELECT MAX (ID)
        INTO v_seq_id
        FROM CAMPAIGN_NOTIFICATION;

      IF v_seq_id IS NULL
      THEN
         v_seq_id := 0;
      END IF;
   END;

   FOR c1_rec IN c1
   LOOP
      BEGIN
         SELECT ID
           INTO v_campaign_notification_id
           FROM CAMPAIGN_NOTIFICATION
          WHERE CAMPAIGN = v_campaign_id AND ITEM = c1_rec.items;
      EXCEPTION
         WHEN NO_DATA_FOUND
         THEN 
           BEGIN
            SELECT owner_ship
             INTO v_dealership_id
             FROM (SELECT   *
                       FROM INVENTORY_TRANSACTION
                      WHERE transacted_item = c1_rec.items
                   ORDER BY transaction_order  DESC) inv_transaction,
                  Service_Provider dealer
            WHERE inv_transaction.owner_ship = dealer.ID AND ROWNUM = 1;
			
         BEGIN
            SELECT 1
              INTO v_temp_id
              FROM CAMPAIGN_NOTIFICATION campnoti
             WHERE campnoti.CAMPAIGN = v_campaign_id
               AND campnoti.ITEM = c1_rec.items
               AND campnoti.DEALERSHIP = v_dealership_id;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               v_seq_id := v_seq_id + 1;

               INSERT INTO CAMPAIGN_NOTIFICATION
                           (ID, notification_status, VERSION, DEALERSHIP,
                            ITEM, CAMPAIGN, CLAIM, D_CREATED_ON, D_CREATED_TIME,
                            D_UPDATED_ON, D_UPDATED_TIME, D_ACTIVE
                           )
                    VALUES (v_seq_id, 'PENDING', 0, v_dealership_id,
                            c1_rec.items, v_campaign_id, NULL, 
                            SYSDATE, CAST( SYSDATE AS TIMESTAMP),
                            SYSDATE, CAST( SYSDATE AS TIMESTAMP), 1
                           );
         END;
      EXCEPTION
         WHEN OTHERS
         THEN
            NULL;
      END;

      END;

      COMMIT;
   END LOOP;

   UPDATE CAMPAIGN
      SET notifications_generated = 1
    WHERE ID = v_campaign_id;

   COMMIT;
END;