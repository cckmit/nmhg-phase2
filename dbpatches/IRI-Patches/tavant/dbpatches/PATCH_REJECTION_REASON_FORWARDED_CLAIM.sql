-- Patch for Default Rejection Reason for OverDue Parts
-- 31st March 2009
-- Author: Ashish.Agarwal
DECLARE
v_config_param_id number;
v_config_option_1 number;
v_config_option_2 number;
v_config_option_3 number;
BEGIN
    select config_param_seq.nextval into v_config_param_id from dual;

    Insert into CONFIG_PARAM
    (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_INTERNAL_COMMENTS, PARAM_DISPLAY_TYPE, D_ACTIVE)
    values
    (v_config_param_id, 'Default Rejection Reason for Forwarded Claim',
    'Default Rejection Reason for Forwarded Claim', 'defaultRejectionReasonForwardedClaim',
    'tavant.twms.domain.common.RejectionReason', 'HUS-MIGRATION', 'select', 1);

   select config_param_option_seq.nextval into v_config_option_1 from dual;

   insert into config_param_option(id,display_value,value) values (v_config_option_1,'R01','R01');
   insert into config_param_options_mapping(id,param_id,option_id)
   values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,v_config_param_id,v_config_option_1);

  --AIR
   INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'AIR',v_config_option_1,1);

    --CLUB CAR ESA
    INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'Clubcar ESA',v_config_option_1,1);

    --CLUB CAR
    INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'Club Car',v_config_option_1,1);

   select config_param_option_seq.nextval into v_config_option_2 from dual;
   insert into config_param_option(id,display_value,value) values (v_config_option_2,'RR1','RR1');
   insert into config_param_options_mapping(id,param_id,option_id)
   values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,v_config_param_id,v_config_option_2);

    --HUSSMANN
    INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'Hussmann',v_config_option_2,1);

    --Transport Solutions ESA
    INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'Transport Solutions ESA',v_config_option_2,1);

   --TFM
   select config_param_option_seq.nextval into v_config_option_3 from dual;
   insert into config_param_option(id,display_value,value) values (v_config_option_3,'D10','D10');
   insert into config_param_options_mapping(id,param_id,option_id)
   values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,v_config_param_id,v_config_option_3);

   INSERT INTO CONFIG_VALUE
    (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO,CONFIG_PARAM_OPTION,d_active)
   VALUES
    (config_value_seq.NEXTVAL, 1,v_config_param_id,'TFM',v_config_option_3,1);

   COMMIT;
   EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
END
/