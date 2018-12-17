--Purpose    : Table which contains the Distance between all the TK Dealerships
--Author     : Jhulfikar Ali A
--Created On : 15-Oct-2008
--Created By : Jhulfikar Ali A

CREATE OR REPLACE PROCEDURE populate_tk_dealer_distance(dealer_id NUMBER) AS

distance NUMBER;
v_dealer_msa NUMBER;
v_dealer_distance REAL;
v_record_exists NUMBER;

CURSOR tk_secondary_dealerships IS
  (SELECT DISTINCT dealer.id dealer,
     msa.id msa
   FROM party party,
     party tkparty,
     dealership dealer,
     address address,
     msa msa
   WHERE party.name = 'TK'
   AND tkparty.is_part_of_organization = party.id
   AND tkparty.id = dealer.id
   AND tkparty.address = address.id
   AND address.zip_code = msa.zip2)
UNION ALL
  (SELECT DISTINCT dealer.id dealer,
     msa.id msa
   FROM party tkparty,
     dealership dealer,
     address address,
     msa msa
   WHERE dealer_number = 'TK'
   AND tkparty.id = dealer.id
   AND tkparty.address = address.id
   AND address.zip_code = msa.zip2)
;

BEGIN

  SELECT msa.id
  INTO v_dealer_msa
  FROM party party,
    address address,
    msa msa
  WHERE party.id = dealer_id
   AND party.address = address.id
   AND address.zip_code = msa.zip2 and 
   msa.country = DECODE(address.country, 'USA', 'US', 'Canada', 'TEMPAC', 'CA', 'TEMPAC', 'Mexico');

  FOR secondary_dealer_rec IN tk_secondary_dealerships
  LOOP

    IF dealer_id = secondary_dealer_rec.dealer THEN
      v_dealer_distance := 0.00;
    ELSE
      v_dealer_distance := gcd_haversine_formula(v_dealer_msa,   secondary_dealer_rec.msa);
    END IF;

    INSERT
    INTO tk_dealerships_distance(id,   primary_dealer,   primary_dealer_msa,   secondary_dealer,   secondary_msa,   distance)
    VALUES(tk_dealerships_distance_seq.nextval,   dealer_id,   v_dealer_msa,   secondary_dealer_rec.dealer,   secondary_dealer_rec.msa,   v_dealer_distance);

    SELECT COUNT(*)
    INTO v_record_exists
    FROM tk_dealerships_distance
    WHERE primary_dealer = secondary_dealer_rec.dealer
     AND secondary_dealer = dealer_id;

  IF v_record_exists > 0 THEN

    UPDATE tk_dealerships_distance
    SET primary_dealer = secondary_dealer_rec.dealer,
      primary_dealer_msa = secondary_dealer_rec.msa,
      secondary_dealer = dealer_id,
      secondary_msa = v_dealer_msa,
      distance = v_dealer_distance
    WHERE primary_dealer = secondary_dealer_rec.dealer
     AND secondary_dealer = dealer_id;
  ELSE

    INSERT
    INTO tk_dealerships_distance(id,   primary_dealer,   primary_dealer_msa,   secondary_dealer,   secondary_msa,   distance)
    VALUES(tk_dealerships_distance_seq.nextval,   secondary_dealer_rec.dealer,   secondary_dealer_rec.msa,   dealer_id,   v_dealer_msa,   v_dealer_distance);

  END IF;

END LOOP;

COMMIT;

END populate_tk_dealer_distance;
/