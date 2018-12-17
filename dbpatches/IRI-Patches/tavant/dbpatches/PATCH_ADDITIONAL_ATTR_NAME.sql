--Patch to enable sorting and filtering of additional attributes by name
--Author : raghuram.d

UPDATE additional_attributes a1 SET name=(
  SELECT a2.name FROM i18nadditional_attribute_name a2
  WHERE a2.additional_attributes_name = a1.id AND a2.locale='en_US'
)
/