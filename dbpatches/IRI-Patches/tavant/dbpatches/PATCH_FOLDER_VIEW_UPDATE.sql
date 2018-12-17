--Purpose    : Inbox view needs to be displayed based on folder views
--Author     : Hari Krishna Y D
--Created On : 27-Jan-09

alter table inbox_view add(FOLDER_NAME VARCHAR2(255))
/
COMMIT
/