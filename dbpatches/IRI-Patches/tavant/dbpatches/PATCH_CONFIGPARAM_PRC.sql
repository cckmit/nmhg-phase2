INSERT INTO CONFIG_PARAM 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON,
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS,
SECTIONS_ORDER, PARAM_ORDER) 
VALUES 
(config_param_seq.NEXTVAL, 'PartReturnStatusToBeConsideredForMaxQty.',
'PartReturnStatusToBeConsideredForMaxQty', 'partReturnStatusConsideredForPRCMaxQty', 'boolean', TO_DATE('11/01/2008
15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', TO_DATE('12/24/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL,
NULL, NULL, NULL, 'select', NULL, NULL, NULL, NULL)
/
INSERT INTO CONFIG_PARAM_OPTION VALUES (config_param_option_seq.NEXTVAL, 'onPartreturn', 'onPartReturn')
/
INSERT INTO CONFIG_PARAM_OPTION VALUES (config_param_option_seq.NEXTVAL, 'onPartShipped', 'onPartShipped')
/
INSERT INTO CONFIG_PARAM_OPTION VALUES (config_param_option_seq.NEXTVAL, 'onPartRecieved', 'onPartRecieved')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING 
SELECT  CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.ID, cpo.ID  FROM CONFIG_PARAM cp, 
CONFIG_PARAM_OPTION cpo WHERE cp.NAME = 'partReturnStatusConsideredForPRCMaxQty' AND cpo.VALUE = 'onPartRecieved'
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING 
SELECT  CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.ID, cpo.ID  FROM CONFIG_PARAM cp, 
CONFIG_PARAM_OPTION cpo WHERE cp.NAME = 'partReturnStatusConsideredForPRCMaxQty' AND cpo.VALUE = 'onPartReturn'
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING 
SELECT  CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.ID, cpo.ID  FROM CONFIG_PARAM cp, 
CONFIG_PARAM_OPTION cpo WHERE cp.NAME = 'partReturnStatusConsideredForPRCMaxQty' AND cpo.VALUE = 'onPartShipped'
/
INSERT INTO CONFIG_VALUE
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION)
VALUES
(config_value_seq.NEXTVAL, 1, NULL, 
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'partReturnStatusConsideredForPRCMaxQty' ), 
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 117247, 'Club Car', NULL, 
NULL,(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'onPartRecieved' ))
/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, 
D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, 
PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
VALUES
(config_param_seq.NEXTVAL, 'denyShipmentGeneratedClaimsCrossedWindowPeriodDays.', 'DenyShipmentGeneratedClaimsCrossedWindowPeriodDays',
'denyShipmentGeneratedClaimsCrossedWindowPeriodDays', 'number', 
TO_DATE('11/01/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', TO_DATE('12/24/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
NULL, NULL, 'textbox', NULL, NULL, 
NULL, NULL)
/
INSERT INTO CONFIG_VALUE
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION)
VALUES
(config_value_seq.NEXTVAL, 1, 0, 
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'denyShipmentGeneratedClaimsCrossedWindowPeriodDays' ), 
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 117247, 'Club Car', NULL, 
NULL,NULL
)
/
COMMIT
/
ALTER TABLE oem_part_replaced ADD part_return_configuration number(19)
/
ALTER TABLE oem_part_replaced ADD CONSTRAINT oem_part_replaced_PRC_FK FOREIGN KEY (part_return_configuration) REFERENCES part_return_configuration
/
COMMIT
/