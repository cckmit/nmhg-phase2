-- Purpose    : Patch to delete dcap related roles from role,user_roles tables
-- Author     : RaviKumar.Y
-- Created On : 18-Apr-2012

DELETE FROM USER_ROLES WHERE USER_ROLES.ROLES=ANY(SELECT ROLE.ID FROM ROLE WHERE ROLE.NAME in('dcapRegionalDirector','dcapDealer','dcapClaimReviewer','dcapAdmin'))
/
DELETE FROM USER_BU_AVAILABILITY where USER_BU_AVAILABILITY.ROLE =ANY(SELECT ROLE.ID FROM ROLE WHERE ROLE.NAME in('dcapRegionalDirector','dcapDealer','dcapClaimReviewer','dcapAdmin'))
/
DELETE FROM EVENT_ROLE_MAPPING WHERE ROLES IN (SELECT ID FROM ROLE WHERE NAME IN ('dcapRegionalDirector','dcapDealer','dcapClaimReviewer','dcapAdmin'))
/
DELETE FROM ROLE WHERE ROLE.NAME in('dcapRegionalDirector','dcapDealer','dcapClaimReviewer','dcapAdmin')
/
COMMIT
/
