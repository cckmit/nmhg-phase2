--Purpose    : Procedure to validate the Part Information provided by dealers and TK
--Author     : Kaushal Soni
--Created On : 07-Sep-2008
--Created By : Jhulfikar Ali A

CREATE OR REPLACE PROCEDURE stg_part_inventory_validation(documentid NUMBER,   p_user_id NUMBER,   p_no_of_error_uploads OUT INT,   p_no_of_successful_uploads OUT INT) AS
CURSOR all_rec IS
SELECT *
FROM stg_parts_inventory
WHERE nvl(validation_status,   'N') = 'N';

CURSOR dealer_no_rec IS
SELECT UNIQUE dealer_no
FROM stg_parts_inventory
WHERE nvl(validation_status,   'N') = 'N';

v_error_code VARCHAR2(4000) := NULL;
v_temp_error_code VARCHAR2(4000) := NULL;
v_var VARCHAR2(255 CHAR) := NULL;
d_var VARCHAR2(255 CHAR) := NULL;
qty NUMBER(8) := 0;
v_error_record_count NUMBER := 0;
v_success_record_count NUMBER := 0;
v_dealer_in_family VARCHAR2(255 CHAR) := NULL;
v_dealer_family_code VARCHAR2(255 byte) := 0;
v_is_admin NUMBER := 1;
v_admin_id NUMBER;
v_dealer_number VARCHAR2(255) := NULL;
v_dealer_number_found NUMBER := 1;
v_is_duplicate_record NUMBER := 1;
v_duplicate_record NUMBER;
v_quantity NUMBER;
BEGIN
  BEGIN
    SELECT ou.id
    INTO v_admin_id
    FROM org_user ou,
      role r,
      user_roles ur
    WHERE ou.id = ur.org_user
     AND r.id = ur.roles
     AND r.name IN('partInventoryAdmin')
     AND ou.id = p_user_id;

  EXCEPTION
  WHEN no_data_found THEN
    v_is_admin := 0;
  WHEN others THEN
    v_error_code := 'Data problem: Contact Tavant Team';
  END;

  FOR each_dealer_rec IN dealer_no_rec
  LOOP
    --SET VARIABLE NULL FOR EACH LOOP
    v_temp_error_code := 'Error:';
    BEGIN
      SELECT dealer_number
      INTO d_var
      FROM dealership d
      WHERE LOWER(d.dealer_number) = LOWER(TRIM(each_dealer_rec.dealer_no));

    EXCEPTION
    WHEN no_data_found THEN
      v_temp_error_code := v_temp_error_code || 'Dealer does not exist';
    WHEN others THEN
      v_temp_error_code := 'Might be Data problem: Contact Tavant Team';
    END;

    IF v_temp_error_code = 'Error:' THEN

      IF LOWER(TRIM(each_dealer_rec.dealer_no)) != 'tk' THEN

        UPDATE part_inventory
        SET quantity = 0
        WHERE dealer =
          (SELECT id
           FROM dealership
           WHERE LOWER(dealer_number) = LOWER(TRIM(each_dealer_rec.dealer_no)))
        AND quantity <> 0;

        COMMIT;
      END IF;

    END IF;

  END LOOP;

  FOR each_rec IN all_rec
  LOOP
    --SET VARIABLE NULL FOR EACH LOOP
    v_error_code := 'Error:';

    BEGIN
      SELECT dealer_number
      INTO d_var
      FROM dealership d
      WHERE LOWER(d.dealer_number) = LOWER(TRIM(each_rec.dealer_no));

    EXCEPTION
    WHEN no_data_found THEN
      v_error_code := v_error_code || 'Dealer does not exist';
    WHEN others THEN
      v_error_code := 'Data problem: Contact Tavant Team';
    END;

    IF v_error_code = 'Error:' THEN

      IF v_is_admin = 1
       AND LOWER(TRIM(each_rec.dealer_no)) != 'tk' THEN
        v_error_code := v_error_code || 'Cannot upload for Dealer';
      END IF;

      IF v_is_admin = 0
       AND LOWER(TRIM(each_rec.dealer_no)) = 'tk' THEN
        v_error_code := v_error_code || 'Cannot upload TK parts';
      END IF;

      IF v_is_admin = 0
       AND LOWER(TRIM(each_rec.dealer_no)) != 'tk' THEN
        BEGIN
          SELECT DISTINCT d.dealer_number
          INTO v_dealer_number
          FROM org_user ou,
            dealership d,
            organization o
          WHERE d.id = o.id
           AND ou.belongs_to_organization = o.id
           AND ou.id = p_user_id;

        EXCEPTION
        WHEN no_data_found THEN
          v_dealer_number_found := 0;
        WHEN others THEN
          v_error_code := 'Data problem: Contact Tavant Team';
        END;

        IF v_dealer_number_found = 1 THEN
          BEGIN
            SELECT dealer_family_code
            INTO v_dealer_family_code
            FROM dealership
            WHERE LOWER(dealer_number) = LOWER(v_dealer_number);

            IF LOWER(v_dealer_family_code) = LOWER(v_dealer_number) THEN
              BEGIN
                SELECT dealer_family_code
                INTO v_dealer_in_family
                FROM dealership d
                WHERE LOWER(d.dealer_number) = LOWER(TRIM(each_rec.dealer_no))
                 AND LOWER(d.dealer_family_code) = LOWER(v_dealer_family_code);

              EXCEPTION
              WHEN no_data_found THEN
                v_error_code := v_error_code || 'Cannot upload other dealer parts';
              WHEN others THEN
                v_error_code := 'Data problem: Contact Tavant Team';
              END;
            ELSE

              IF LOWER(TRIM(each_rec.dealer_no)) != LOWER(v_dealer_number) THEN
                v_error_code := v_error_code || 'Cannot upload other dealer parts';
              END IF;

            END IF;

          EXCEPTION
          WHEN no_data_found THEN

            IF LOWER(TRIM(each_rec.dealer_no)) != LOWER(v_dealer_number) THEN
              v_error_code := v_error_code || 'Cannot upload other dealer parts';
            END IF;

          WHEN others THEN
            v_error_code := 'Data problem: Contact Tavant Team';
          END;
        ELSE
          v_error_code := v_error_code || 'Dealer number does not exist';
        END IF;

      END IF;

    END IF;

    BEGIN
      SELECT item_number
      INTO v_var
      FROM item
      WHERE LOWER(item_number) = LOWER(TRIM(each_rec.part_number))
       AND make = 'TK';

    EXCEPTION
    WHEN no_data_found THEN
      v_error_code := v_error_code || 'Part does not exist:';
    WHEN others THEN
      v_error_code := 'Data problem: Contact Tavant Team';
    END;

    BEGIN
      v_quantity := to_number(TRIM(each_rec.quantity));

      IF LENGTH(TRIM(TRANSLATE(each_rec.quantity,   '0123456789',   ' '))) IS
      NOT NULL OR v_quantity < 0 OR TRIM(each_rec.quantity) IS
      NULL THEN
        v_error_code := v_error_code || 'Quantity is not valid:';
      END IF;

    EXCEPTION
    WHEN others THEN
      v_error_code := v_error_code || 'Quantity is not valid:';
    END;

    BEGIN
      SELECT COUNT(id)
      INTO v_duplicate_record
      FROM stg_parts_inventory
      WHERE TRIM(dealer_no) = TRIM(each_rec.dealer_no)
       AND TRIM(part_number) = TRIM(each_rec.part_number)
       AND(validation_status = 'N' OR validation_status = 'Y');
      -- Part Id from Procedure;
    END;

    IF v_duplicate_record >= 1 THEN
      v_error_code := 'Error:Duplicate Record Found';
    END IF;

    IF TRIM(each_rec.dealer_no) IS
    NOT NULL OR TRIM(each_rec.part_number) IS
    NOT NULL OR TRIM(each_rec.quantity) IS
    NOT NULL THEN

      IF v_error_code = 'Error:' THEN

        UPDATE stg_parts_inventory
        SET validation_status = 'Y',
          validation_error = '',
          validation_date = sysdate,
          document_id = documentid
        WHERE id = each_rec.id;

        v_success_record_count := v_success_record_count + 1;
      ELSE

        UPDATE stg_parts_inventory
        SET validation_status = 'N',
          validation_error = v_error_code,
          validation_date = sysdate,
          document_id = documentid
        WHERE id = each_rec.id;

        v_error_record_count := v_error_record_count + 1;
      END IF;

    ELSE

      DELETE FROM stg_parts_inventory
      WHERE id = each_rec.id;
    END IF;

    --COMMIT FOR EACH LOOP
    COMMIT;
  END LOOP;

  p_no_of_error_uploads := v_error_record_count;
  p_no_of_successful_uploads := v_success_record_count;
  --FINAL COMMIT
  COMMIT;
END;
/