package tavant.twms.web.actions;

import java.util.Collections;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * An implementation of Action support for JQ grid 
 * @author prasad.r
 */
public abstract class AbstractGridAction extends I18nActionSupport {

    private int page;
    private int rows;
    private String sidx;
    private String sord;
    private Map<String, String[]> searchParams;
    public static final String SORT_ASC_PARAM = "asc";
    protected static final Logger logger = Logger.getLogger(AbstractGridAction.class);

    protected ListCriteria getCriteria() {
        ListCriteria criteria = new ListCriteria();

        //We have to use page-1 'coz page index starts at 1 for JQGrid, but 0 for PageSpecification.
        PageSpecification pageSpec = new PageSpecification(page - 1, rows);
        criteria.setPageSpecification(pageSpec);

        if (StringUtils.hasText(sidx)) {
            criteria.addSortCriteria(sidx, SORT_ASC_PARAM.equalsIgnoreCase(sord));
        }

        if (searchParams != null) {
            for (Map.Entry<String, String[]> searchParamEntry : searchParams.entrySet()) {
                criteria.addFilterCriteria(searchParamEntry.getKey(), searchParamEntry.getValue()[0]);
            }
        }

        return criteria;
    }

    public String gridBody() {
        PageResult<?> pageResult = getBody();
        if(pageResult == null)
            pageResult = getEmptyPageResult();
        JSONObject gridJson = new JSONObject();
        try {
            gridJson.put("total", pageResult.getNumberOfPagesAvailable());
            gridJson.put("page", page);

            gridJson.put("records", pageResult.getPageSpecification().getTotalRecords());

            JSONArray jsonArray = new JSONArray();

            for (Object result : pageResult.getResult()) {
                JSONObject row = new JSONObject();
                transformRowData(result, row);
                jsonArray.put(row);
            }

            gridJson.put("rows", jsonArray);
        } catch (JSONException e) {
            logger.error("Error while transforming to JSON String !!!!", e);
            throw new RuntimeException("Error while transforming to JSON String !!!!", e);
        }

        return writeJsonResponse(gridJson);
    }

   protected PageResult getEmptyPageResult() {
        PageSpecification emptyPageSpec = new PageSpecification(0, rows, 0);
        return new PageResult(Collections.EMPTY_LIST, emptyPageSpec, 0);
    }
    
    protected abstract PageResult<?> getBody();

    protected abstract void transformRowData(Object result, JSONObject row) throws JSONException;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Map<String, String[]> getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(Map<String, String[]> searchParams) {
        this.searchParams = searchParams;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }
    
}
