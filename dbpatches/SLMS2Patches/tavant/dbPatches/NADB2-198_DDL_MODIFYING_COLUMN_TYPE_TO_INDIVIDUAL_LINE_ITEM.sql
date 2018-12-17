--Purpose    : Patch TO MODIFY COLUMN TYPE OF Individual_Line_Item
--Author     : AJIT KUMAR SINGH
--Created On : 22-AUG-2014

alter table Individual_Line_Item add non_oemParts NUMBER(19)
/
update Individual_Line_Item set non_oemParts=NON_OEM_PART_REPLACED
/
alter table Individual_Line_Item drop column NON_OEM_PART_REPLACED
/
alter table Individual_Line_Item add NON_OEM_PART_REPLACED VARCHAR2(255 CHAR)
/
declare
cursor c1 is 
select noem.description,ili.id from Individual_Line_Item ili,NON_OEM_PART_REPLACED noem
where (ili.non_oemParts = noem.id );
begin
for each_rec in c1 loop 
update Individual_Line_Item set NON_OEM_PART_REPLACED=each_Rec.description where id = each_Rec.id;
commit;
end loop;
end;
/
commit
/