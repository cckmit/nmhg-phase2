--PURPOSE    : PATCH TO CHANGE MISCELLANEOUS PARTS SECTION AND COST CATEGORY NAME as a part of 4.3 upgrade 
--AUTHOR     : Rashmi Malik	
--CREATED ON : 21-Jun-2010

UPDATE SECTION SET NAME = 'Miscellaneous Parts' WHERE NAME = 'MiscellaneousParts'
/
UPDATE COST_CATEGORY SET NAME = 'Miscellaneous Parts' WHERE NAME = 'MiscellaneousParts'
/
COMMIT
/
