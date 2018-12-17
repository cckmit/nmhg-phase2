-- Add column called price_updated in installed parts and set default value for existing parts as false
-- Created By : AJITKUMAR.SINGH
-- Created on date 07 Oct 2013

alter table
   installed_parts
add
   (
   price_updated  NUMBER(1)  
   )
   /   
   update installed_parts set price_updated=0
   /


  
