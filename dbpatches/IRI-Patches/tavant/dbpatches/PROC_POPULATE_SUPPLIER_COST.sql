--Purpose    : To populate supplier cost column from actual cost in the cost line items table
--Author     : Jitesh Jain
--Created On : 10-Mar-09

CREATE OR REPLACE PROCEDURE POPULATE_SUPPLIER_COST
AS
CURSOR all_rec IS
SELECT cost_amt, cost_curr, id
FROM cost_line_item
where supplier_cost_amt is null;

BEGIN

  FOR each_rec IN all_rec
  LOOP
 update cost_line_item 
 set supplier_cost_amt = each_rec.cost_amt,
 supplier_cost_curr = each_rec.cost_curr
 where id = each_rec.id;

 commit;
  END LOOP;
END;
/
BEGIN
POPULATE_SUPPLIER_COST();
END;
/
COMMIT
/