--Purpose    : Updating payment table to update amount of 'claimed_amount_amt' and 'total_amount_amt' column  which is same as 'base_amt' column of line_item_group table
--Author     : ajitkumar.singh
--Created On : 12/06/2011
DECLARE
  CURSOR cur_sel
  IS
    SELECT Lig.accepted_amt,
      pmt.id
    FROM line_item_group lig,
      line_item_groups ligs,
      payment Pmt
    WHERE Lig.Id                = Ligs.Line_Item_Groups
    AND Ligs.For_Payment        = Pmt.Id
    AND lig.name                = 'Claim Amount'
    AND pmt.total_amount_amt    <> lig.accepted_amt ;
  v_base_amt NUMBER(19,2)  := 0.0;
BEGIN
  FOR i IN cur_sel
  LOOP
    SELECT SUM(lig.base_amt)
    INTO v_base_amt
    FROM line_item_groups ligs,
      line_item_group lig
    WHERE ligs.line_item_groups = lig.id
    AND ligs.for_payment        = i.id
    AND lig.name               <> 'Claim Amount';
    
    UPDATE payment Pmt
    SET claimed_amount_amt = v_base_amt,
      total_amount_amt     = i.accepted_amt
    WHERE id               = i.id;
  END LOOP;
  commit;
END;
/
Commit
/
