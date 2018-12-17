--Purpose: Added an 'orphan' column to indicate whether a Document is currently attached to an entity or not.
--Author: Vikas Sasidharan
--Created On: Date 28 Jul 2009

alter table document add (orphan number(1,0) default 1)
/
commit
