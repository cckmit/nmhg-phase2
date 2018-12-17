--PURPOSE   :UPDATE LABOR CHARGES APPLICABLE TO SMR & FPI CLAIMS MODIFICATIONS
--AUTHOR    :SANTISWAROOP.K
--CREATED ON:22-OCT-2012
DECLARE
CURSOR C2 IS SELECT name FROM business_unit;
	buName VARCHAR2(255);
	trueValueId NUMBER(19,0);
	falseValueId NUMBER(19,0);
	valueInsertId NUMBER(19,0);
	configParamId NUMBER(19,0);
BEGIN
   SELECT id INTO trueValueId FROM config_param_option WHERE value='true';
    SELECT id INTO falseValueId FROM config_param_option WHERE value='false';
    SELECT id INTO configParamId  FROM config_param cp WHERE cp.name='laborChargesApplicableOnlyForSmrAndFpiClaims';
 FOR businessUnitName IN C2 LOOP
   buName := businessUnitName.name;
   IF buName ='EMEA' THEN 
	valueInsertId := falseValueId;
    ELSE
    valueInsertId := trueValueId;
    END IF;
	UPDATE config_value cv SET cv.config_param_option = valueInsertId WHERE cv.config_param = configParamId AND cv.business_unit_info = buName;
 END LOOP;
 COMMIT;
  EXCEPTION
  WHEN OTHERS
  THEN
  Rollback;
END;
/
UPDATE config_param cp
SET cp.description='This decides the payment of labor charges for all claim types except SMR approved claim and Field Modification Claim 
and by default it should be true',
  cp.display_name ='Pay Labor Charges',
  cp.name         ='payLaborCharges'
WHERE cp.name     ='laborChargesApplicableOnlyForSmrAndFpiClaims'
/
COMMIT
/