--Purpose    : Patch for adding DISCOUNT TYPE column in WARRANTY table
--Author     : Priyanka S
--Created On : 27-NOV-2013

alter table warranty add (discount_type Number(19))
/
alter table warranty add constraint discount_type_fk foreign key (discount_type) references list_of_values(Id)
/
alter table WARRANTY add (DISCOUNT_NUMBER varchar2(2000))
/
alter table WARRANTY add (DISCOUNT_PERCENTAGE varchar2(2000))
/