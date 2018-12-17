--Purpose    : Added tables for locale and i18n domain rule text
--Author     : shraddha.nanda
--Created On : 19-July-08

create table product_locale (LOCALE varchar2(255), DESCRIPTION varchar2(255))
/
insert into product_locale values('en_EN','Message in English(UK)')
/
insert into product_locale values('en_US','Message in English(US)')
/
insert into product_locale values('de_DE','Message in German')
/
insert into product_locale values('fr_FR','Message in French')
/
create table i18nDomain_rule_text (ID number(19), LOCALE varchar2(255), FAILURE_DESCRIPTION varchar2(255))
/
CREATE SEQUENCE  "I18NDOMAINRULETEXT_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 100 NOCACHE  NOORDER  NOCYCLE 
/
ALTER TABLE i18nDomain_rule_text ADD (DOMAIN_RULE NUMBER(19))
/
ALTER TABLE i18nDomain_rule_text ADD CONSTRAINT I18N_DOMAIN_RULE_FK FOREIGN KEY (DOMAIN_RULE) REFERENCES DOMAIN_RULE(ID)
/
commit
/