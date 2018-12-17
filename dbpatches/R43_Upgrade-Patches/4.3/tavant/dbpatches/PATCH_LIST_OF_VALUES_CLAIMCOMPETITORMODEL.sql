--Purpose    : Updating some of cost categories whose code and description is not matching length (This is required for DealerAPI claim submission)
--Author     : Devendra Babu N
--Created On : 17-Nov-2010

update list_of_values
set description=code
where type='CLAIMCOMPETITORMODEL' and length(code) != length(description)
/
COMMIT
/