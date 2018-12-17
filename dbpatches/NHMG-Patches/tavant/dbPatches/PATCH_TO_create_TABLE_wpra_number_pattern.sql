--PURPOSE    : PATCH TO create TABLE wpra_number_pattern  
--AUTHOR     : Raghu
--CREATED ON : 10-Apr-13

CREATE TABLE wpra_number_pattern (
  id                  NUMBER(19,0)       NOT NULL,
  is_active           NUMBER(1,0)        NULL,
  numbering_pattern   NUMBER(19,0)       NULL,
  template            VARCHAR2(255 CHAR) NULL,
  d_created_on        DATE               NULL,
  d_internal_comments VARCHAR2(255 CHAR) NULL,
  d_updated_on        DATE               NULL,
  d_last_updated_by   NUMBER(19,0)       NULL,
  d_created_time      TIMESTAMP(6)       NULL,
  d_updated_time      TIMESTAMP(6)       NULL,
  d_active            NUMBER(1,0)        DEFAULT 1 NULL,
  business_unit_info  VARCHAR2(255 CHAR) NULL,
  pattern_type        VARCHAR2(20)       NULL,
  sequence_name       VARCHAR2(255)      NULL
)
  STORAGE (
    NEXT       1024 K
  )
/
ALTER TABLE wpra_number_pattern
  ADD CONSTRAINT wpra_number_pattern_pk PRIMARY KEY (
    id
  )
  USING INDEX
    STORAGE (
      NEXT       1024 K
    )
/
ALTER TABLE wpra_number_pattern
  ADD CONSTRAINT wpra_number_pattern_bu_fk FOREIGN KEY (
    business_unit_info
  ) REFERENCES business_unit (
    name
  )
/
ALTER TABLE wpra_number_pattern
  ADD CONSTRAINT wpra_num_patt_lst_updt_by_fk FOREIGN KEY (
    d_last_updated_by
  ) REFERENCES org_user (
    id
  )
/
COMMIT
/