--PURPOSE    : PATCH_TO_UPDATE_DESCRIPTION_FOR_SMR
--AUTHOR     : Jyoti Chauhan
--CREATED ON : 07-MAY-13


update config_param set DESCRIPTION='Can Dealer File SMR Claim' where name='smrClaimAllowed'
/
commit
/