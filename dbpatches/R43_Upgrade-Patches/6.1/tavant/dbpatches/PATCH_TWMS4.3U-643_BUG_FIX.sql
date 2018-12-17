--Purpose    : Updating payment table to update currency of 'claimed_amount_curr' and 'total_amount_curr' column  which is same as 'base_curr' column of line_item_group table
--Author     : ajitkumar.singh,joseph
--Created On : 27/05/2011
declare 
cursor cur_sel is
select
      Lig.Base_Curr   ,pmt.id   
    FROM
      line_item_group lig,
      line_item_groups ligs,
      payment Pmt      
    WHERE
      Lig.Id                     = Ligs.Line_Item_Groups
    AND Ligs.For_Payment         = Pmt.Id
    and lig.name                 = 'Claim Amount'
    and nvl(pmt.claimed_amount_curr,-99) <> lig.base_curr;
    
    
begin
	for i in cur_sel
	loop
		update
		  payment Pmt
		set
		   claimed_amount_curr = i.base_curr,
			total_amount_curr = i.base_curr
		where id =  i.id;
	end loop;
commit;
end;
/
Commit
/