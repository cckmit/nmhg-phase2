--Purpose    : Changed update_tree_info to consider scheme as same item group can exist in multiple schemes
--Created On : 15-Mar-2010
--Created By : Rahul Katariya
--Impact     : Item groups

CREATE OR REPLACE
PROCEDURE "UPDATE_TREE_INFO"(
    root_group_name IN VARCHAR2,
    v_scheme_name   IN VARCHAR2,
    buName          IN VARCHAR2)
AS
  STACK_RECORD STACK%ROWTYPE;
  counter     NUMBER :=0;
  max_counter NUMBER :=0;
  current_top NUMBER :=0;
  temp        NUMBER :=0;
  CNT         NUMBER :=0;
  LOOP_FLAG   NUMBER :=0;
  V_Tree_Id   NUMBER :=0;
  v_scheme_id NUMBER :=0;
BEGIN
  BEGIN
    IF (LENGTH(Buname) = 0 OR Buname = '') THEN
      SELECT Id
      INTO V_Scheme_Id
      FROM Item_Scheme
      WHERE Name             = V_Scheme_Name
      AND Business_Unit_Info = Root_Group_Name;
      SELECT Tree_Id
      INTO v_tree_id
      FROM Item_Group
      WHERE group_code       = root_group_name
      AND scheme             = v_scheme_id
      AND Business_Unit_Info = root_group_name;
    ELSE
      SELECT Id
      INTO V_Scheme_Id
      FROM Item_Scheme
      WHERE Name             = V_Scheme_Name
      AND Business_Unit_Info = Buname;
      SELECT tree_id
      INTO v_tree_id
      FROM item_group
      WHERE group_code       = root_group_name
      AND scheme             = v_scheme_id
      AND Business_Unit_Info = Buname;
    END IF;
  END;
  BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE TREE';
    EXECUTE IMMEDIATE 'TRUNCATE TABLE STACK';
    COMMIT;
  END;
  BEGIN
    INSERT INTO TREE
    SELECT ID,
      is_part_of
    FROM ITEM_GROUP
    WHERE TREE_ID = v_tree_id
    AND scheme    = v_scheme_id;
    BEGIN
      SELECT COUNT(*) INTO temp FROM TREE;
    END;
    BEGIN
      counter     := 2;
      max_counter := 2 * (temp);
      current_top := 1;
      INSERT INTO STACK
      SELECT 1, emp, 1, NULL,0 FROM TREE WHERE boss IS NULL;
      DELETE FROM TREE WHERE boss IS NULL;
    END;
    BEGIN
      WHILE counter <= (max_counter) -- UPDATING BUSINESS UNIT AS WELL
      LOOP
        BEGIN
          SELECT COUNT(*)
          INTO CNT
          FROM STACK S1,
            TREE T1
          WHERE S1.emp     = T1.boss
          AND S1.stack_top = current_top;
          IF CNT           > 0 THEN
            BEGIN -- push when top has subordinates, set lft value
              INSERT INTO STACK
              SELECT (current_top + 1),
                MIN(T1.emp) ,
                counter ,
                NULL ,
                0
              FROM STACK S1,
                TREE T1
              WHERE S1.emp     = T1.boss
              AND S1.stack_top = current_top;
              DELETE
              FROM TREE
              WHERE emp =
                (SELECT emp FROM STACK WHERE stack_top = current_top + 1
                );
              counter     := counter     + 1;
              current_top := current_top + 1;
            END;
          ELSE
            BEGIN -- pop the stack and set rgt value
              UPDATE STACK
              SET rgt         = counter,
                stack_top     = -stack_top -- pops the stack
              WHERE stack_top = current_top;
              counter        := counter     + 1;
              current_top    := current_top - 1;
            END;
          END IF;
        END;
      END LOOP;
    END;
  END;
  BEGIN
    UPDATE STACK SET rgt=0 WHERE rgt IS NULL;
  END;
  BEGIN
    SELECT COUNT(*) INTO LOOP_FLAG FROM STACK WHERE PROCESSED =0 AND ROWNUM<2;
    WHILE (LOOP_FLAG > 0)
    LOOP
      BEGIN
        SELECT A.* INTO STACK_RECORD FROM STACK A WHERE PROCESSED = 0 AND ROWNUM<2;
        BEGIN
          UPDATE ITEM_GROUP
          SET lft  =STACK_RECORD.LFT,
            rgt    =STACK_RECORD.RGT
          WHERE ID = STACK_RECORD.EMP;
          UPDATE STACK SET PROCESSED = 1 WHERE EMP = STACK_RECORD.EMP;
          SELECT COUNT(*) INTO LOOP_FLAG FROM STACK WHERE PROCESSED = 0 AND ROWNUM<2;
        END;
      END;
    END LOOP;
    COMMIT;
  END;
  BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE TREE';
    EXECUTE IMMEDIATE 'TRUNCATE TABLE STACK';
    COMMIT;
  END;
END;
/