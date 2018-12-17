-- PURPOSE    : PATCH TO RENAME 'FLAG FOR CLAIM MANUAL REVIEW' TO 'FLAG FOR AUDIT CLAIM';
-- AUTHOR     : Priyanka S.
-- CREATED ON : 13-MARCH-2014
update config_param set display_name='Flag for Audit Claim' where name ='flagForManualReviewClaim'
/
commit
/