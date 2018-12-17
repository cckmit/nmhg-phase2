--Purpose    : Used to keep all the upload data
--Author     : Jhulfikar Ali. A
--Created On : 24-Dec-08

INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure,
             upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'installBaseUpload',
             'Install Base Upload', 'Upload Scheme - Install Base Upload',
             '.\pages\secure\admin\upload\templates\Template-InstallBaseUpload.xls', 'STG_INSTALL_BASE', 'POPULATE_STG_INSTALL_BASE', 
             'TAV_DC_026_VALIDATION', 'TAV_DC_026_UPLOAD'
            )
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display,
             description, template_path, staging_table, staging_procedure, 
             validation_procedure,
             upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'stdExtWntyCvg',
             'Standard Extended Warranty Coverage',
             'Upload Scheme - Standard Extended Warranty Coverage', 
             '.\pages\secure\admin\upload\templates\Template-STDEXTWarrantyCvgUpload.xls',
             '', '', '', ''
            )
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display,
             description, template_path, staging_table, staging_procedure, 
             validation_procedure,
             upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'copyJobCodeFaultFound',
             'Copy Job Codes And Fault Found',
             'Upload Scheme - Copy Job Codes And Fault Found', 
             '.\pages\secure\admin\upload\templates\Template-CopyJobCodesAndFaultFound.xls',
             '', '', '', ''
            )
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure, upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'uploadJobCodes',
             'Upload Job Codes', 'Upload Scheme - Upload Job Codes',
             '.\pages\secure\admin\upload\templates\Template-UploadJobCodes.xls', '', '', '', ''
            )
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure, upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'draftWarrantyClaims',
             'Draft Warranty Claims', 'Upload Scheme - Draft Warranty Claims',
             '.\pages\secure\admin\upload\templates\Template-DraftWarrantyClaims.xls', '', '', '', ''
            )
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure, upload_procedure
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'partSourceHistory',
             'Part Source History Information', 'Upload Scheme - Part Source History Information',
             '.\pages\secure\admin\upload\templates\Template-PartSourceHistoryInformation.xls', '', '', '', ''
            )
/
COMMIT
/