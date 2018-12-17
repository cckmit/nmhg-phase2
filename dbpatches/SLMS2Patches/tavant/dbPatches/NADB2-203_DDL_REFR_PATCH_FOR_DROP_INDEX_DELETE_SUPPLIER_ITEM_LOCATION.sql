--Purpose    : Scripts for the fix related to SLMSPROD-1663
--Author     : Arpitha Nadig AR
--Created On : 03-SEP-2014
drop index ITEM_IDX
/
create index supplier_item_location_mapping on supplier_item_location(item_mapping)
/
declare
cursor c1 is
select item_mapping,count(1) cc from supplier_item_location
group by item_mapping having count(1)>1;
begin
  for each_rec in c1 loop
    delete from supplier_item_location where item_mapping = each_Rec.item_mapping and rownum < each_rec.cc;
    commit;
  end loop;
end;
/
