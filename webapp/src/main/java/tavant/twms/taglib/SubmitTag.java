package tavant.twms.taglib;

/**
 * Creates a submit button.
 * 
 * @see Button
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class SubmitTag extends ButtonTag {
    public void populateParams() {
        super.populateParams();
        Button submitButton = (Button) component;
        submitButton.setType("submit");
    }
}
