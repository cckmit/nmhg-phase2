--Purpose    : Update campaign status for existing campaigns present in database 
--Author     : Hari Krishna Y D
--Created On : 02 Jan 2009

create or replace
PROCEDURE PROC_UPDATE_CAMPAIGN_STATUS AS
CURSOR c1 IS
SELECT *
FROM campaign;
BEGIN

  FOR c1_rec IN c1
  LOOP
    BEGIN

      UPDATE campaign
      SET status =
      CASE
      WHEN notifications_generated IS NULL THEN
        'Draft'
      WHEN notifications_generated = 0 THEN
        'Draft'
      ELSE
        'Active'
      END
      WHERE id = c1_rec.id;

      COMMIT;
    END;
  END LOOP;
END PROC_UPDATE_CAMPAIGN_STATUS;
/
BEGIN
PROC_UPDATE_CAMPAIGN_STATUS();
END;
/