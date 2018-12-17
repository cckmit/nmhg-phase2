--Purpose    : INSERT SCRIPT FOR mktg_groups_lookup FOR DEALER mg CODE 074
--Author     : Arpitha Nadig AR
--Created On : 08-SEP-2014
INSERT
INTO mktg_groups_lookup
  (
    id,
    truck_mktg_group_code,
    claim_type,
    warranty_type,
    dealer_mktg_group_code,
    d_created_on,
    d_updated_on,
    d_internal_comments,
    d_active,
    d_last_updated_by,
    d_created_time,
    d_updated_time
  )
  VALUES
  (
    mktg_groups_lookup_seq.nextval,
    '011',
    'PARTS',
    'STANDARD',
    '074',
    sysdate,
    sysdate,
    'AMER-DataMigration For SLMSPROD-1721',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
/
INSERT
INTO mktg_groups_lookup
  (
    id,
    truck_mktg_group_code,
    claim_type,
    warranty_type,
    dealer_mktg_group_code,
    d_created_on,
    d_updated_on,
    d_internal_comments,
    d_active,
    d_last_updated_by,
    d_created_time,
    d_updated_time
  )
  VALUES
  (
    mktg_groups_lookup_seq.nextval,
    '074',
    'PARTS',
    'STANDARD',
    '011',
    sysdate,
    sysdate,
    'AMER-DataMigration For SLMSPROD-1721',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
/
commit
/