--Purpose    : Patch for renaming 'Part Recieved' to 'Part Received' 
--Author     : Priyanka S
--Created On : 28-MAY-2014
update event_state set display_name ='Part Received' where name = 'DUE_PART_RETURN_RECEIPT'
/
commit
/