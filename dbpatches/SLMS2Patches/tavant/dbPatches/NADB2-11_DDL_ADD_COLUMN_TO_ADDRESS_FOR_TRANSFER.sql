--Purpose    : Patch for adding the Customer Contact Title column in address_for_transfer table
--Author     : Raghavendra
--Created On : 27-NOV-2013

alter table address_for_transfer add (customer_contact_title varchar2(2000))
/