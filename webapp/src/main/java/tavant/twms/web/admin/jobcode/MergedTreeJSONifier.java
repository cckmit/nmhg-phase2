package tavant.twms.web.admin.jobcode;

import org.json.JSONException;
import org.json.JSONObject;
import tavant.twms.domain.failurestruct.ActionNode;

/**
 * @author : janmejay.singh
 *         Date: Jul 4, 2007
 *         Time: 8:12:39 PM
 */
public class MergedTreeJSONifier extends AssemblyTreeJSONifier {
    @Override
    protected JSONObject getJSONObject(ActionNode action, Filter filter) throws JSONException {
        JSONObject jsonObject = super.getJSONObject(action, filter);
        jsonObject.getJSONObject(DEFINITION).put(ID, action.getServiceProcedure().getDefinition().getId());
        return jsonObject;
    }
}
