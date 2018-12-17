--Purpose    : Patch for updating domain_predicate which has attribute as dcapClaim.filedOn since the attribute has been refactored to filedOnDate
--Author     : Pradyot Rout
--Created On : 10-Jul-09

declare CURSOR c1
IS
  SELECT *
  FROM domain_predicate
  where predicate_asxml LIKE '%dcapClaim.filedOn%';
BEGIN
  FOR c1_rec IN c1
  LOOP
    BEGIN
      update domain_predicate set 
      predicate_asxml= replace(c1_rec.predicate_asxml,'dcapClaim.filedOn','dcapClaim.filedOnDate')
      where id=c1_rec.id;
    END;
    COMMIT;
  END LOOP;
END;
/
COMMIT
/