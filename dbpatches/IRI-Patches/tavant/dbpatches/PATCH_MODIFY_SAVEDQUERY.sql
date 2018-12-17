--Purpose    : PATCH FOR ADDING THE COLUMNS CONTEXT AND SEARCH_QUERY_NAME INTO SAVED_QUERY TABLE
--Author     : Shraddha Nanda
--Created On : 10-SEPT-08

alter table saved_query add 
(search_query_name varchar2(50))
/
alter table saved_query add 
(context varchar2(30))
/