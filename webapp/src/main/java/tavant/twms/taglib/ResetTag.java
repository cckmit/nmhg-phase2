package tavant.twms.taglib;

/**
 * Creates a reset button.
 * 
 * @see Button
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class ResetTag extends ButtonTag {
    public void populateParams() {
        super.populateParams();
        Button submitButton = (Button) component;
        submitButton.setType("reset");
    }
}
