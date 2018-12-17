--Purpose    : Increased the size of Description column for Migration activity
--Author     : Jhulfikar Ali. A
--Created On : 29-Apr-09

alter table I18NCAMPAIGN_TEXT modify (description varchar2(500))
/
commit
/