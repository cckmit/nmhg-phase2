--Purpose    : ROLE SCHEME PATCH
--Author     : anandvijay.k
--Created On : 11-Jul-08

create table role_group (id number(19,0) not null, d_created_on date, d_internal_comments varchar2(255 char), d_updated_on date, description varchar2(255 char), name varchar2(255 char) not null, depth number(10,0) not null, lft number(10,0) not null, rgt number(10,0) not null, tree_id number(19,0) not null, version number(10,0) not null, is_part_of number(19,0), scheme number(19,0), d_last_updated_by number(19,0), primary key (id))
/

create table role_scheme (id number(19,0) not null, business_unit_info varchar2(255 char), d_created_on date, d_internal_comments varchar2(255 char), d_updated_on date, name varchar2(255 char) not null, version number(10,0) not null, d_last_updated_by number(19,0), primary key (id))
/

create table role_scheme_purposes (role_scheme number(19,0) not null, purposes number(19,0) not null, primary key (role_scheme, purposes), unique (purposes))
/

create table roles_in_group (role_group number(19,0) not null, role number(19,0) not null, primary key (role_group, role))
/

create sequence ROLE_GROUP_SEQ start with 1000 increment by 20
/

create sequence ROLE_SCHEME_SEQ start with 1000 increment by 20
/

alter table role add display_name varchar(255)
/

alter table role_group add constraint ROLE_GROUP_UPDATEDBY_FK foreign key (d_last_updated_by) references org_user(id)
/

alter table role_group add constraint ROLE_GROUP_ISPARTOF_FK foreign key (is_part_of) references role_group(id)
/

alter table role_group add constraint ROLE_GROUP_SCHEME_FK foreign key (scheme) references role_scheme(id)
/

alter table role_scheme add constraint ROLE_SCHEME_UPDATEDBY_FK foreign key (d_last_updated_by) references org_user(id)
/

alter table role_scheme_purposes add constraint ROLE_SCHEME_PURPOSE_PUR_FK foreign key (purposes) references purpose(id)
/

alter table role_scheme_purposes add constraint ROLE_SCHEME_PURPOSE_SCH_FK foreign key (role_scheme) references role_scheme(id)
/

alter table roles_in_group add constraint ROLESIN_GRP_ROLEGRP_FK foreign key (role_group) references role_group
/

alter table roles_in_group add constraint ROLESIN_GRP_ROLE_FK foreign key (role) references role(id)
/