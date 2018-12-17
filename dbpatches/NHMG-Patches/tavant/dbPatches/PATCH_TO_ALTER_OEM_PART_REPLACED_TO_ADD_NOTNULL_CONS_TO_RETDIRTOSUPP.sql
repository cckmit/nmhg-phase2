--PURPOSE    : PATCH TO ALTER oem_part_replaced to add not null constraint for return_directly_to_supplier
--AUTHOR     : Raghavendra
--CREATED ON : 08-Feb-13

ALTER TABLE oem_part_replaced MODIFY (return_directly_to_supplier NUMBER(1,0) CONSTRAINT return_to_supp_not_null_cons NOT NULL)
/