--PURPOSE    : PATCH FOR altering the sequence properly so that there won't be any constraint violation issues
--AUTHOR     : Hari Krishna Y D
--CREATED ON : 03-MAR-09

ALTER SEQUENCE supplier_locations_seq INCREMENT BY 10000
/
SELECT supplier_locations_seq.NEXTVAL FROM DUAL
/
COMMIT
/
ALTER SEQUENCE supplier_locations_seq
INCREMENT BY 20
  NOCYCLE
  CACHE 20
  NOORDER
/
COMMIT
/