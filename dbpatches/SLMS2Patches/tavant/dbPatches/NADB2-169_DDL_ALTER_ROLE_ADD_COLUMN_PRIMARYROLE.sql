--PURPOSE    : New Column PRIMARY_ROLE to ROLE table, PRIMARY_ROLE flag to differentiate between primary role and non - primary.
--AUTHOR     : Chetan
--CREATED ON : 03-JUN-2014
alter table ROLE add (
  PRIMARY_ROLE NUMBER(1,0) default 1 not null
)
/