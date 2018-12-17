--Purpose    : Item upload errors
--Author     : raghuram.d
--Created On : 29-Jul-09

BEGIN
    create_upload_error('itemUpload','en_US','BUSINESS UNIT','IT001','Business Unit is not specified');
    create_upload_error('itemUpload','en_US','BUSINESS UNIT','IT002','Business Unit is not valid');
    create_upload_error('itemUpload','en_US','ITEM NUMBER','IT003','Item Number is not specified');
    create_upload_error('itemUpload','en_US','ITEM DESC','IT004','Item Desc is not specified');
    create_upload_error('itemUpload','en_US','ITEM GROUP CODE','IT005','Item Group Code is not specified');
    create_upload_error('itemUpload','en_US','IS SERIALIZED','IT006','Is Serialized is not specified');
    create_upload_error('itemUpload','en_US','IS SERIALIZED','IT007','Is Serialized is not valid');
    create_upload_error('itemUpload','en_US','PART MANUFACTURING CODE','IT008','Part Manufacturing Code is not specified');
    create_upload_error('itemUpload','en_US','ITEM STATUS','IT009','Item Status is not specified');
    create_upload_error('itemUpload','en_US','ITEM STATUS','IT010','Item Status is not valid');
    create_upload_error('itemUpload','en_US','HAS HOUR METER','IT011','Has Hour Meter is not specified');
    create_upload_error('itemUpload','en_US','HAS HOUR METER','IT012','Has hour Meter is not valid');
    create_upload_error('itemUpload','en_US','OWNER','IT013','Owner is not valid');
    create_upload_error('itemUpload','en_US','ITEM GROUP CODE','IT014','Item Group Code is not valid');
    create_upload_error('itemUpload','en_US','UPDATES','IT015','Updates is not valid');
    create_upload_error('itemUpload','en_US','ITEM NUMBER','IT016','Duplicate item in upload');
    create_upload_error('itemUpload','en_US','ITEM NUMBER','IT017','Item already exists');
    create_upload_error('itemUpload','en_US','ITEM NUMBER','IT018','Item does not exist');
    create_upload_error('itemUpload','en_US','UNIT OF MEASURE','IT019','Unit Of Measure is not specified');
END;
/