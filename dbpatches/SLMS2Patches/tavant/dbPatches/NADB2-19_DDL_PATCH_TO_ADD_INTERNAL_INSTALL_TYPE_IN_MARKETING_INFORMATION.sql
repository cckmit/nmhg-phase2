-- Patch to create column Internal_Install_Type in Marketing_Information
-- Author		: ParthaSarathy R
-- Created On	: 18-Dec-2013

alter table MARKETING_INFORMATION ADD (INTERNAL_INSTALL_TYPE NUMBER(19, 0))
/