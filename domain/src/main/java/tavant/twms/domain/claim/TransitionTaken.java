package tavant.twms.domain.claim;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 11/11/13
 * Time: 9:57 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TransitionTaken {

    DENY("Deny"),
    HOLD("Hold");

    private String transitionTaken;

    private  TransitionTaken(String transitionTaken){
        this.transitionTaken = transitionTaken;
    }

    public String getTransitionTaken() {
        return transitionTaken;
    }

    @Override
    public String toString() {
        return this.transitionTaken;
    }
}
