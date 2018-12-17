package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import static tavant.twms.taglib.TaglibUtil.getInt;

import java.io.Writer;

/**
 * @author janmejay.singh
 * Date: Apr 18, 2007
 * Time: 12:48:43 PM
 */
public class Loop extends Component {

    private int repeat;
    private String status;
    private int index = 0;

    protected Loop(ValueStack valueStack) {
        super(valueStack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean start(Writer writer) {
        stack.getContext().put(status, index);
        super.start(writer);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean end(Writer writer, String string) {
        end(writer, string, false);
        if(++index < repeat) {
            stack.getContext().put(status, index);
            return true;
        }
        stack.getContext().remove(status);
        return false;
    }

    public void setRepeat(String repeat) {
        this.repeat = getInt(findValue(repeat, Integer.class));
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
