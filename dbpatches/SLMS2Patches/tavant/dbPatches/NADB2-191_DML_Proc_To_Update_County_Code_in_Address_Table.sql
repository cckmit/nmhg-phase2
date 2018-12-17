DECLARE
  CURSOR c1
  IS
    SELECT ccm.county_code county_code,
      ccm.county_name county_name,
      a.state state,
      a.county address_county,
      a.id id
    FROM address a,
      county_code_mapping ccm
    WHERE a.country           ='US'
    AND LENGTH(a.county)     <> 3
    AND upper(ccm.state)      = upper(a.state)
    AND upper(ccm.county_name)= upper(a.county);
BEGIN
  FOR c1_rec IN c1
  LOOP
    UPDATE address
    SET county         =c1_rec.county_code,
      county_code_name = c1_rec.county_code ||'-'||c1_rec.county_name
    WHERE c1_rec.id    =id;
  END LOOP;
END;
