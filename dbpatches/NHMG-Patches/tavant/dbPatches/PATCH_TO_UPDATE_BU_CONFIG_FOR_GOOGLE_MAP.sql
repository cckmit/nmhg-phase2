--PURPOSE    : PATCH_TO_UPDATE_BU_CONFIG_FOR_GOOGLE_MAP
--AUTHOR     : Jyoti Chauhan
--CREATED ON : 03-MAY-13


update config_param set display_name='Enable Google Maps for Travel Hours' where display_name='Enable Goodle Maps for Travel Hours'
/
commit
/