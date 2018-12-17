package tavant.twms.taglib;

/**
 * Creates a Input button.
 * 
 * @see Button
 *
 * @author prasad.r
 */
@SuppressWarnings("serial")
public class InputButtonTag extends ButtonTag {
    public void populateParams() {
        super.populateParams();
        Button submitButton = (Button) component;
        submitButton.setType("button");
    }
}