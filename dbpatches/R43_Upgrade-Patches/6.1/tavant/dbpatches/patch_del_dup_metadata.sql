DECLARE
  CURSOR all_rec
  IS
    SELECT column_name,
      column_type,
      column_order,
      upload_mgt,
      COUNT(1)
    FROM upload_mgt_meta_data
    GROUP BY column_name,
      column_type,
      column_order,
      upload_mgt
    HAVING COUNT(1)>1;
BEGIN
  FOR each_REc IN all_rec
  LOOP
    BEGIN
      DELETE
      FROM upload_mgt_meta_data
      WHERE column_name=each_rec.column_name
      AND column_type  =each_rec.column_type
      AND column_order =each_rec.column_order
      AND upload_mgt   =each_rec.upload_mgt
      AND rownum       =1;
    END;
  END LOOP;
  COMMIT;
END;