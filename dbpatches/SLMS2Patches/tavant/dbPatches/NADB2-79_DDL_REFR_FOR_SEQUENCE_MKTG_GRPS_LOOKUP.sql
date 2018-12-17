-- PURPOSE    : PATCH TO Create Sequence for MKTG_GROUPS_LOOKUP TABLE
-- AUTHOR     : Arpitha Nadig AR.
-- CREATED ON : 26-FEB-2014
CREATE SEQUENCE MKTG_GROUPS_LOOKUP_SEQ
  MINVALUE 1000
  MAXVALUE 9999999999999999999999999999
  INCREMENT BY 20
  NOCYCLE
  NOORDER
  CACHE 20
/