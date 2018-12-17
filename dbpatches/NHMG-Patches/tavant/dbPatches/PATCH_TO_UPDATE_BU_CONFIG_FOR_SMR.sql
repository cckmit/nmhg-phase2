--PURPOSE    : PATCH_TO_UPDATE_BU_CONFIG_FOR_SMR
--AUTHOR     : Jyoti Chauhan
--CREATED ON : 07-MAY-13


update config_param set display_name='Can Dealer File SMR Claim' where name='smrClaimAllowed'
/
commit
/