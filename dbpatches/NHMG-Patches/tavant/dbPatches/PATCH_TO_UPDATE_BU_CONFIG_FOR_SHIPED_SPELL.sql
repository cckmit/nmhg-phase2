--PURPOSE    : PATCH_TO_UPDATE_BU_CONFIG_FOR_SHIPED_SPELL
--AUTHOR     : Jyoti Chauhan
--CREATED ON : 10-MAY-13


update config_param set display_name='Waiting Part Returns to be Denied If Not Shipped', description='Waiting Part Returns to be Denied If Not Shipped' where name='waitingPartReturnsToBeDeniedIfNotShiped'
/
commit
/