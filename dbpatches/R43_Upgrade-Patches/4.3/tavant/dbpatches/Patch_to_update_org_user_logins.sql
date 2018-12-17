--Name: Joseph
--Date: 23 June 2011
--Impact: Updates logins to avoid login merge while looking up login during the migration
create table update_login(old_login varchar2(100), new_login varchar2(100))
/
insert into update_login values('100','tkd_100')
/
insert into update_login values('aaffolter','inactive_aaffolter')
/
insert into update_login values('acm','tkd_acm')
/
insert into update_login values('amber','tkd_amber')
/
insert into update_login values('archenoul','tkd_archenoul')
/
insert into update_login values('bcox','d_bcox')
/
insert into update_login values('bronwyn','tkd_bronwyn')
/
insert into update_login values('bschiffman','tkd_bschiffman')
/
insert into update_login values('cameron','d_cameron')
/
insert into update_login values('carlos','d_carlos')
/
insert into update_login values('charlie','d_charlie')
/
insert into update_login values('clarence','tkd_clarence')
/
insert into update_login values('clarke','tkd_clarke')
/
insert into update_login values('craigb','d_craigb')
/
insert into update_login values('danwhite','tkd_danwhite')
/
insert into update_login values('denver','tkd_denver')
/
insert into update_login values('esteban','tkd_esteban')
/
insert into update_login values('felipe','d_felipe')
/
insert into update_login values('frigomax','tkd_frigomax')
/
insert into update_login values('gary','tkd_gary')
/
insert into update_login values('gil','d_gil')
/
insert into update_login values('gkaladamis','tkd_gkaladamis')
/
insert into update_login values('gladys','tkd_gladys')
/
insert into update_login values('guillermo','d_guillermo')
/
insert into update_login values('guitton','tkd_guitton')
/
insert into update_login values('jadzia','tkd_jadzia')
/
insert into update_login values('jason','d_jason')
/
insert into update_login values('jeff','d_jeff')
/
insert into update_login values('jeffs','d_jeffs')
/
insert into update_login values('jennifer','tkd_jennifer')
/
insert into update_login values('jgk_hartge','tkd_jgk_hartge')
/
insert into update_login values('jim','d_jim')
/
insert into update_login values('jmartinez','d_jmartinez')
/
insert into update_login values('jmg','tkd_jmg')
/
insert into update_login values('johnh','d_johnh')
/
insert into update_login values('johnlindop','tkd_johnlindop')
/
insert into update_login values('jose','d_jose')
/
insert into update_login values('jprzybylski','tkd_jprzybylski')
/
insert into update_login values('juan','d_juan')
/
insert into update_login values('juanjo','d_juanjo')
/
insert into update_login values('kenny','d_kenny')
/
insert into update_login values('kennym','tkd_kennym')
/
insert into update_login values('kevin','d_kevin')
/
insert into update_login values('manuel','tkd_manuel')
/
insert into update_login values('maria','tkd_maria')
/
insert into update_login values('mario','tkd_mario')
/
insert into update_login values('martin','d_martin')
/
insert into update_login values('michele','tkd_michele')
/
insert into update_login values('michelle','tkd_michelle')
/
insert into update_login values('mlawlor','tkd_mlawlor')
/
insert into update_login values('mlinarek','tkd_mlinarek')
/
insert into update_login values('omar','d_omar')
/
insert into update_login values('pablo','d_pablo')
/
insert into update_login values('perez','d_perez')
/
insert into update_login values('pete','tkd_pete')
/
insert into update_login values('rodney','tkd_rodney')
/
insert into update_login values('ross','tkd_ross')
/
insert into update_login values('scott','d_scott')
/
insert into update_login values('sosbourne','tkd_sosbourne')
/
insert into update_login values('stevel','d_stevel')
/
insert into update_login values('techniker','d_techniker')
/
insert into update_login values('tecnoservice','tkd_tecnoservice')
/
insert into update_login values('tka','inactive_tka')
/
insert into update_login values('tony','d_tony')
/
insert into update_login values('s1022','sup27416')
/
insert into update_login values('s1023','sup27418')
/
insert into update_login values('s1026','sup27657')
/
insert into update_login values('s1029','sup27659')
/
insert into update_login values('s1030','sup27693')
/
insert into update_login values('s1044','sup28217')
/
insert into update_login values('s1055','sup28298')
/
insert into update_login values('s1056','sup28300')
/
insert into update_login values('s1066','sup29130')
/
insert into update_login values('s1068','sup29341')
/
insert into update_login values('s1076','sup29385')
/
insert into update_login values('s1077','sup29391')
/
insert into update_login values('s1080','sup29599')
/
insert into update_login values('s1081','sup29730')
/
insert into update_login values('s1083','sup29800')
/
insert into update_login values('s1085','sup29875')
/
insert into update_login values('s1089','sup29952')
/
insert into update_login values('s1096','sup30070')
/
insert into update_login values('s1100','sup30158')
/
insert into update_login values('s1119','sup30368')
/
insert into update_login values('s1123','sup30375')
/
insert into update_login values('s1127','sup30405')
/
insert into update_login values('s1257','sup33250')
/
insert into update_login values('s1258','sup33251')
/
insert into update_login values('s1260','sup33252')
/
insert into update_login values('s1263','sup33255')
/
insert into update_login values('s1264','sup33256')
/
insert into update_login values('s1276','sup33263')
/
insert into update_login values('s1304','sup33290')
/
insert into update_login values('s1313','sup33382')
/
insert into update_login values('s1375','sup34700')
/
insert into update_login values('s1377','sup34796')
/
insert into update_login values('s1379','sup34801')
/
insert into update_login values('s1383','sup34828')
/
insert into update_login values('s1397','sup34985')
/
insert into update_login values('s1398','sup34990')
/
insert into update_login values('s1406','sup35115')
/
insert into update_login values('s1410','sup35118')
/
insert into update_login values('s1431','sup36380')
/
insert into update_login values('s1439','sup39325')
/
insert into update_login values('s1441','sup39350')
/
insert into update_login values('s1453','sup39860')
/
insert into update_login values('s1458','sup41200')
/
insert into update_login values('s1461','sup41490')
/
insert into update_login values('s1465','sup42020')
/
insert into update_login values('s1467','sup42080')
/
insert into update_login values('s1477','sup42382')
/
insert into update_login values('s1478','sup42400')
/
insert into update_login values('s1481','sup42408')
/
insert into update_login values('s1482','sup42410')
/
insert into update_login values('s1483','sup42415')
/
insert into update_login values('s1486','sup42417')
/
insert into update_login values('s1487','sup42420')
/
insert into update_login values('s1490','sup42470')
/
insert into update_login values('s1495','sup43210')
/
insert into update_login values('s1496','sup43219')
/
insert into update_login values('s1498','sup43448')
/
insert into update_login values('s1499','sup13116')
/
insert into update_login values('s1500','sup43614')
/
insert into update_login values('s1501','sup44090')
/
insert into update_login values('s1525','sup48089')
/
insert into update_login values('s1526','sup48116')
/
insert into update_login values('s1532','sup49218')
/
insert into update_login values('s1533','sup50005')
/
insert into update_login values('s1539','sup51151')
/
insert into update_login values('s1541','sup51152')
/
insert into update_login values('s1547','sup51157')
/
insert into update_login values('s1569','sup51921')
/
insert into update_login values('s1570','sup52000')
/
insert into update_login values('s1600','sup60012')
/
insert into update_login values('s1604','sup60015')
/
insert into update_login values('s1640','sup60041')
/
insert into update_login values('s1851','sup60244')
/
insert into update_login values('s1860','sup60251')
/
insert into update_login values('s1862','sup60253')
/
insert into update_login values('s1906','sup60296')
/
insert into update_login values('s1923','sup60311')
/
insert into update_login values('s1938','sup60325')
/
insert into update_login values('s1975','sup68901')
/
insert into update_login values('s2064','sup123123')
/
insert into update_login values('s394','sup10811')
/
insert into update_login values('s395','sup10830')
/
insert into update_login values('s396','sup10831')
/
insert into update_login values('s404','sup10905')
/
insert into update_login values('s407','sup10920')
/
insert into update_login values('s410','sup10941')
/
insert into update_login values('s431','sup11462')
/
insert into update_login values('s509','sup13116')
/
insert into update_login values('s513','sup13377')
/
insert into update_login values('s534','sup14058')
/
insert into update_login values('s668','sup17810')
/
insert into update_login values('s753','sup19759')
/
insert into update_login values('s757','sup20005')
/
insert into update_login values('s762','sup20050')
/
insert into update_login values('s764','sup20079')
/
insert into update_login values('s784','sup20710')
/
insert into update_login values('s787','sup20794')
/
insert into update_login values('s805','sup21530')
/
insert into update_login values('s817','sup21640')
/
insert into update_login values('s820','sup21671')
/
insert into update_login values('s821','sup21672')
/
insert into update_login values('s918','sup23171')
/
insert into update_login values('s937','sup23946')
/
insert into update_login values('s951','sup24575')
/
insert into update_login values('s967','sup24998')
/
insert into update_login values('s969','sup25000')
/
insert into update_login values('s1027','sup27658')
/
insert into update_login values('s1091','sup30021')
/
insert into update_login values('s15053','sup15053')
/
INSERT INTO UPDATE_LOGIN VALUES('s389','sup10796')
/
COMMIT
/
UPDATE ORG_USER B SET B.LOGIN = 
(
select a.NEW_LOGIN from UPDATE_LOGIN a where LOWER(a.OLD_LOGIN) = LOWER(nvl(B.LOGIN,'-99')) 
)
WHERE EXISTS
(
select 1 from UPDATE_LOGIN C where LOWER(C.OLD_LOGIN) = LOWER(nvl(B.LOGIN,'-99'))
)
/
commit
/
UPDATE JBPM_TASKINSTANCE B SET B.ACTORID_ = 
(
SELECT A.new_LOGIN FROM UPDATE_LOGIN A WHERE lower(A.old_login) = lower(B.ACTORID_) 
)
WHERE EXISTS
(
select 1 from UPDATE_LOGIN C where lower(C.OLD_LOGIN) = lower(B.ACTORID_)
)
/
commit
/