--changes made for enterprise dealers
alter TABLE service_provider ADD enterprise_dealer  NUMBER(1,0)    
/
ALTER TABLE service_provider ADD part_of NUMBER 
/
ALTER TABLE service_provider
  ADD CONSTRAINT service_provider_partOf_fk FOREIGN KEY (
    part_of
  ) REFERENCES service_provider (
    id
  )
/