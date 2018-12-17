--patch for insertion of business unit configuration to decide whether to show Generic Attachment or not
INSERT INTO CONFIG_PARAM (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, PARAM_DISPLAY_TYPE) VALUES (config_param_seq.nextval, 'This configuration decides whether Generic Attachment should be enabled and shown for current business or not', 'Enable Generic Attachment','enableGenericAttachment','boolean','radio')
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID)  VALUES (CONFIG_PARAM_OPTION_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableGenericAttachment'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  (ID, PARAM_ID, OPTION_ID) VALUES (CONFIG_PARAM_OPTION_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'enableGenericAttachment'),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'))
/