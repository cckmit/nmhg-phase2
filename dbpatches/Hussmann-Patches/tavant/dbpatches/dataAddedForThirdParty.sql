--Purpose: Patch for adding data to the Organixation and Party in sync to the third_pary 
--Author: P Shraddha Nanda
--Created On: Date 10 Nov 2008

insert into party values (1240457,'THIRD PARTY',1,null,524532,null,null,null,null,null,null)
/
insert into organization values (1240457,'USD' )
/
insert into service_provider values (1240457, 123)
/
insert into third_party values (1240457, 123)
