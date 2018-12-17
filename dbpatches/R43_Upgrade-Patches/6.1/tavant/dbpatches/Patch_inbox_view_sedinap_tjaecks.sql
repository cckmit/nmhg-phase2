--Name : DEVENDRA
--Date : 23 June 2011
--Impact : Updates field names for sedinap and tjaecks
-- Qrys applied for sedinap login
declare
begin
  update INBOX_VIEW set FIELD_NAMES = FIELD_NAMES ||',lastUpdatedOnDate,model' where id = 101100000002208;
  if sql%ROWCOUNT = 1 then
     dbms_output.put_line('in 1');
    delete DEFAULT_FOLDER_VIEW where id = 1100000002680;
    delete INBOX_VIEW where id = 1100000002800;
  end if;
--Qrys applied tjaecks login
update INBOX_VIEW 
set FIELD_NAMES = 'workOrderNumber,payment.claimedAmount,enum:ClaimState:state,forDealer.name,serialNumber,clmTypeName,payment.activeCreditMemo.creditMemoDate' 
where id = 101100000002747;
if sql%ROWCOUNT = 1 then
dbms_output.put_line('in 2');
  delete default_folder_view where id = 1100000001560;
  delete INBOX_VIEW where id = 1100000001480;
end if;
end;
/
commit
/