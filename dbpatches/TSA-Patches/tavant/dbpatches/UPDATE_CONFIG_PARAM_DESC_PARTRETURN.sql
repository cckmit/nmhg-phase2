--PURPOSE    : PATCH FOR ADDING NEW CONFIG PARAM FOR PART RETURN VISIBILITY FOR DEALERS
--AUTHOR     : Amritha Krishnamoorthy
--CREATED ON : 21-DEC-09
UPDATE CONFIG_PARAM 
SET description = 'Enable the Part Return receiver to correct the part number entered by dealer'
WHERE  name = 'enablePartOffFeature'
/
UPDATE CONFIG_PARAM 
SET description = 'Enable Dealers to enter Barcodes while returning parts'
WHERE  name = 'enableBarCodeFeature'
/
COMMIT
/


