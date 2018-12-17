--Purpose    : Config param for replacing BU part with non BU part
--Author     : ramalakshmi.p	
--Created On : 08-Feb-2010

INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'In a claims component replaced section can a BU part be replaced by non-BU part?', 'Can BU Part be replaced by non-BU Part','canBUPartBeReplacedByNonBUPart','boolean',TO_DATE('02/08/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'TSA-Configuration', TO_DATE('02/08/2010 10:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
'', 'CLAIMS', 'radio', 1, 'CLAIM_INPUT_PARAMETERS', 1,1,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canBUPartBeReplacedByNonBUPart'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='canBUPartBeReplacedByNonBUPart'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE values (CONFIG_VALUE_SEQ.nextval,1,'',(select id from CONFIG_PARAM where NAME='canBUPartBeReplacedByNonBUPart'),sysdate,
'TSA-Migration',sysdate,56,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'Thermo King TSA',(select id from CONFIG_PARAM_OPTION where value='true'))
/
ALTER TABLE installed_parts add (serial_number varchar2(255))
/
ALTER TABLE SERIALIZED_ITEM_REPLACEMENT ADD (FOR_COMPOSITION NUMBER(19))
/
ALTER TABLE SERIALIZED_ITEM_REPLACEMENT
  ADD CONSTRAINT SLZED_ITEM_REP_ITEMCOMP_FK FOREIGN KEY (FOR_COMPOSITION) 
  REFERENCES inventory_item_composition (ID)
/
ALTER TABLE SERIALIZED_ITEM_REPLACEMENT ADD (NEW_PART NUMBER(19))
/
ALTER TABLE SERIALIZED_ITEM_REPLACEMENT
  ADD CONSTRAINT SLZED_ITEM_REP_INVITEM_FK FOREIGN KEY (NEW_PART) 
  REFERENCES inventory_item (ID)
/
COMMIT
/