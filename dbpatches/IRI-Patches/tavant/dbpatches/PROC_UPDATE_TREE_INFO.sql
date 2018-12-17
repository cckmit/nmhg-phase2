--Purpose    : Changed to proc to work for multiples business units
--Author     : Hari Krishna Y D
--Created On : 14-March-09


CREATE OR REPLACE
PROCEDURE UPDATE_TREE_INFO
  (
    v_group_code IN VARCHAR2 )
AS
  STACK_RECORD STACK%ROWTYPE;
  counter     NUMBER :=0;
  max_counter NUMBER :=0;
  current_top NUMBER :=0;
  temp        NUMBER :=0;
  CNT         NUMBER :=0;
  LOOP_FLAG   NUMBER :=0 ;
  v_tree_id   NUMBER := 0;
BEGIN
  BEGIN
     SELECT id INTO v_tree_id FROM item_group WHERE group_code = v_group_code;
  END;
  BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE TREE';
    EXECUTE IMMEDIATE 'TRUNCATE TABLE STACK';
    COMMIT;
  END;
  BEGIN
     INSERT INTO TREE
     SELECT ID,is_part_of FROM ITEM_GROUP WHERE TREE_ID = v_tree_id;
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
            WHERE S1.emp   = T1.boss
          AND S1.stack_top = current_top;
          IF CNT           > 0 THEN
            BEGIN -- push when top has subordinates, set lft value
               INSERT INTO STACK
               SELECT (current_top + 1),
                MIN(T1.emp)            ,
                counter                ,
                NULL                   ,
                0
                 FROM STACK S1,
                TREE T1
                WHERE S1.emp   = T1.boss
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
              SET rgt           = counter,
                stack_top       = -stack_top -- pops the stack
                WHERE stack_top = current_top;
              
              counter     := counter     + 1;
              current_top := current_top - 1;
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
          SET lft    =STACK_RECORD.LFT,
            rgt      =STACK_RECORD.RGT
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
