--purpose : PATCH FOR Changes to domain Rule
--Author: Jitesh Jain
--Created On: Date 21 Mar 2009

create table category_domain_predicate as 
select * from domain_predicate where predicate_asxml like '%forCategory%'
/
create table parts_domain_predicate as
select * from domain_predicate where predicate_asxml like '%InstalledParts%'
/
delete from domain_rule_audit where domain_rule in (
select id from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%InstalledParts%'))
/
delete from saved_query where domain_predicate in (
select id from domain_predicate where predicate_asxml like '%InstalledParts%')
/
delete from I18NDOMAIN_RULE_TEXT where domain_rule in (
select id from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%InstalledParts%'))
/
delete from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%InstalledParts%')
/
delete from domain_predicate where predicate_asxml like '%InstalledParts%'
/
delete from domain_rule_audit where domain_rule in (
select id from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%forCategory%'))
/
delete from saved_query where domain_predicate in (
select id from domain_predicate where predicate_asxml like '%forCategory%')
/
delete from I18NDOMAIN_RULE_TEXT where domain_rule in (
select id from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%forCategory%'))
/
delete from domain_rule where predicate in (
select id from domain_predicate where predicate_asxml like '%forCategory%')
/
delete from domain_predicate where predicate_asxml like '%forCategory%'
/
commit