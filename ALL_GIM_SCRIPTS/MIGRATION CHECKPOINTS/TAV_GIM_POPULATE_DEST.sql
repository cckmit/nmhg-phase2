
PROMPT
PROMPT Message Next process == Start loading the destination tables from the staging tables
PROMPT
PROMPT Message Press 'ENTER' 2 or more times to proceed to populate the target database or close this window to 'ABORT'
PAUSE
PAUSE
PAUSE
PAUSE
PAUSE
PAUSE

PROMPT Loading detination in progress.Please wait...

commit;


@@TAV_GIM_POPULATE_DESTINATION_AREA.sql;

PROMPT
PROMPT Message Loading target tables completed !
PROMPT
PROMPT Message For more information query Master Log and Exception log tables.Rebuilding indexes for jbpm..
PROMPT
@@Mark_Index_USABLE.sql;

PROMPT Message For more information query Master Log and Exception log tables.Rebuilding indexes for jbpp completed!!

