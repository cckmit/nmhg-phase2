alter table HYDROMETER_TEST add  (Gravity_Diff_Below_Cutoff_Eu         NUMBER(10),
Gravity_Diff_Above_Cutoff_Eu         NUMBER(10))
/
update HYDROMETER_TEST set gravity_diff_below_cutoff_eu= specific_gravity_difference
/
commit
/
alter table HYDROMETER_TEST add  (cutoff_Energy_Units         NUMBER(10))
/
alter table discharge_TEST add  (cutoff_Energy_Units         NUMBER(10))
/
alter table cell_specific_gravity add  (low_sg             NUMBER(1))
/
alter table HYDROMETER_TEST add  (default_Low_SG         NUMBER(10))
/
update HYDROMETER_TEST set default_low_sg=1100
/
commit
/
