-- patch: TWMS4.3U-489
Insert Into 
  Warehouse_Warehouse_Bins 
Values 
  ((Select W.Id 
    From Warehouse W, Location L 
    Where L.Code = 'THERMO KING/HUSSMANN AFTERMARKET   ATTEN: HUSSMANN RETURN /MRN RACK' 
    And L.Id = W.Location), 
  'OTHER')
/
Commit
/