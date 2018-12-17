--Purpose    : Patch for correcting the conflicted BUs,changes made as a part of 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 13-June-2011

--Days for Forwarded Claim Denied
UPDATE CONFIG_VALUE
SET VALUE          = 30
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForForwardedClaimDenied'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/
--Causal Items on Claim Configuration 
DELETE
FROM CONFIG_VALUE
WHERE CONFIG_PARAM_OPTION =
  (SELECT ID FROM CONFIG_PARAM_OPTION WHERE DISPLAY_VALUE = 'Part'
  )
AND CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'causalItemsOnClaimConfiguration'
  )
AND BUSINESS_UNIT_INFO = 'TFM'
/
DELETE
FROM CONFIG_VALUE
WHERE CONFIG_PARAM_OPTION =
  (SELECT ID FROM CONFIG_PARAM_OPTION WHERE DISPLAY_VALUE = 'Part'
  )
AND CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'replacedItemsOnClaimConfiguration'
  )
AND BUSINESS_UNIT_INFO = 'TFM'
/
--If default processor needs to be shown to Dealer on all system generated audits
UPDATE CONFIG_VALUE
SET CONFIG_PARAM_OPTION =
  (SELECT ID FROM CONFIG_PARAM_OPTION WHERE DISPLAY_VALUE = 'Yes'
  )
WHERE CONFIG_PARAM =
  (SELECT ID
  FROM CONFIG_PARAM
  WHERE NAME = 'isDefaultProcessorShownOnAutoReplies'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann' 
/
UPDATE CONFIG_VALUE
SET VALUE          = 1
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'numberOfClaimResubmitAllowed'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/
UPDATE CONFIG_VALUE
SET VALUE          = 7
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysConfiguredForYellowWarning'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/
UPDATE CONFIG_VALUE
SET VALUE          = 14
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysConfiguredForRedWarning'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/

UPDATE CONFIG_VALUE
SET VALUE          = 30
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignYellowWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Clubcar ESA'
/
UPDATE CONFIG_VALUE
SET VALUE          = 90
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignYellowWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/
UPDATE CONFIG_VALUE
SET VALUE          = 60
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignYellowWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Transport Solutions ESA'
/
UPDATE CONFIG_VALUE
SET VALUE          = 15
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignRedWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Clubcar ESA'
/
UPDATE CONFIG_VALUE
SET VALUE          = 60
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignRedWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Hussmann'
/
UPDATE CONFIG_VALUE
SET VALUE          = 30
WHERE CONFIG_PARAM =
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForCampaignRedWarningWindow'
  )
AND BUSINESS_UNIT_INFO = 'Transport Solutions ESA'
/
UPDATE CONFIG_VALUE
SET CONFIG_PARAM_OPTION =
  (SELECT ID FROM CONFIG_PARAM_OPTION WHERE DISPLAY_VALUE = 'Yes'
  )
WHERE CONFIG_PARAM =
  (SELECT ID
  FROM CONFIG_PARAM
  WHERE NAME = 'isMarketInfoApplicable'
  )
AND BUSINESS_UNIT_INFO = 'Thermo King TSA' 
/
commit
/