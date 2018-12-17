--Purpose    : Patch for adding the Customer Contact Title column in address table
--Author     : Priyanka S
--Created On : 27-NOV-2013

alter table address add (CUSTOMER_CONTACT_TITLE varchar2(2000))
/