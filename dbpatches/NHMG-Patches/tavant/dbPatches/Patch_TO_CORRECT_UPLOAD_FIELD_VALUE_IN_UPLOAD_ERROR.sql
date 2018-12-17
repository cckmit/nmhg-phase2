--Purpose    : Patch TO CORRECT UPLOAD FIELD VALUE IN UPLOAD ERROR TABLE
--Author     : ROHIT MEHROTRA
--Created On : 26-JUNE-2013

update upload_error set upload_field='PART NUMBER' where upload_field='PART ITEM NUMBER' AND code like 'DC%'
/
update upload_error set upload_field='TRUCK SERIAL NUMBER' where upload_field='SERIAL NUMBER' AND code like 'DC%'
/
update upload_error set upload_field='HOURS ON TRUCK' where upload_field='HOURS IN SERVICE' AND code like 'DC%'
/
update upload_error set upload_field='REPAIR END DATE' where upload_field='REPAIR DATE' AND code like 'DC%'
/
update upload_error set upload_field='Part Fitted Date' where upload_field='INSTALLATION DATE' AND code like 'DC%'
/
update upload_error set upload_field='Dealer Job Number' where upload_field='WORK ORDER NUMBER' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts Quantity' where upload_field='REPLACED IR PARTS QUANTITY' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts' where upload_field='REPLACED_IR_PARTS' AND code like 'DC%'
/
update upload_error set upload_field='Installed OEM Parts Quantity' where upload_field='INSTALLED IR PARTS QUANTITY' AND code like 'DC%'
/
update upload_error set upload_field='Replaced Non OEM Parts quantity' where upload_field='REPLACED NON IR PARTS QUANTITY' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts' where upload_field='REPLACED IR PARTS' AND code like 'DC%'
/
update upload_error set upload_field='Installed OEM Parts' where upload_field='INSTALLED IR PARTS' AND code like 'DC%'
/
update upload_error set upload_field='Replaced OEM Parts Serial Num' where upload_field='REPLACED IR PARTS SERIAL NUM' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts Quantity' where upload_field='REPLACED_IR_PARTS_QUANTITY' AND code like 'DC%'
/
update upload_error set upload_field='Replaced Non OEM Parts' where upload_field='REPLACED NON IR PARTS' AND code like 'DC%'
/
update upload_error set upload_field='Is Part Installed on OEM' where upload_field='IS PARAT INSTALLED ON TKTSA' AND code like 'DC%'
/
update upload_error set upload_field='Hours on Truck when part Installed' where upload_field='HOURS ON TRUCK DURING INSTALLATION' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts' where upload_field='Replaced IR Parts' AND code like 'DC%'
/
update upload_error set upload_field='Replaced OEM Parts Serial Num' where upload_field='Replaced IR Parts Serial Num' AND code like 'DC%'
/
update upload_error set upload_field='Removed OEM Parts Quantity' where upload_field='Replaced IR Parts Quantity' AND code like 'DC%'
/
update upload_error set upload_field='Installed OEM Parts Quantity' where upload_field='Installed IR Parts Quantity' AND code like 'DC%'
/
update upload_error set upload_field='Installed OEM Parts Serial Num' where upload_field='Installed IR Parts Serial Num' AND code like 'DC%'
/
update upload_error set upload_field='TRUCK SERIAL NUMBER' where upload_field='Serial Number' AND code like 'DC%'
/
update upload_error set upload_field='Installed OEM Parts' where upload_field='Installed IR Parts' AND code like 'DC%'
/
commit
/