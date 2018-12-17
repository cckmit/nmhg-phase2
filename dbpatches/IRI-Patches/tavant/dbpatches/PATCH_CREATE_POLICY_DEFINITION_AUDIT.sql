--Purpose    : Created  a new table to store the Policy Definition Audit Details
--Author     : vamshi.gunda
--Created On : 19-Jun-09

create table Policy_Definition_Audit
 (id number(19,0) not null, 
 comments varchar2(4000 char), 
 action_Taken varchar2(50 char), 
 for_Policy_Definition number(19,0) not null,
 list_index number(3,0),
 d_created_on date, 
 d_updated_on date, 
 d_created_time date,
 d_updated_time date, 
 d_internal_comments varchar2(255 char),  
 version number(10,0) not null, 
 d_last_updated_by number(19,0),
 D_ACTIVE number(1,0),
 constraint fk_for_Policy_Definition FOREIGN KEY (for_Policy_Definition)
			REFERENCES Policy_Definition(id)
 )
/
