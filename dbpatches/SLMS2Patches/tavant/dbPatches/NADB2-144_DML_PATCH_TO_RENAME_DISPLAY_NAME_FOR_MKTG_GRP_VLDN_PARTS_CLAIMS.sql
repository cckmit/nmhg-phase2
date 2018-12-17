--PURPOSE    : PATCH_TO_UPDATE_DISPLAY_NAME_FOR_MARKETING GROUP_VALIDTION_FOR_PARTS_CLAIMS.
--AUTHOR     : Arpitha Nadig AR
--CREATED ON : 09-MAY-2014
update config_param set display_name='Allowed Dealer Marketing Group Codes For Filing Parts Claims' where name='allowedDealerMktgGroupCodes'
/
commit
/