package tavant.twms.annotations.form;

/**
 * @author : janmejay.singh
 *         Date: Jul 17, 2007
 *         Time: 3:19:34 PM
 */

/**
 * These constants are used by the form framework to plugin the right jsp for actions.
 */
public enum ActionType {

    /**
     * show the create page.
     */
    CREATE_REQUEST,

    /**
     * submit the create request
     */
    CREATE_SUBMIT,

    /**
     * update request for existing entity
     */
    UPDATE_REQUEST,

    /**
     * submit of update request for existing entity
     */
    UPDATE_SUBMIT,

    /**
     * read existing entity
     */
    READ_ONLY,

    /**
     * delete request for existing entity
     */
    DELETE_REQUEST,

    /**
     * submit for delete request of existing entity
     */
    DELETE_SUBMIT,

    /**
     * submit the activate request
     */
    ACTIVATE_SUBMIT,

    /**
     * submit the de-activate request
     */
    DEACTIVATE_SUBMIT
}
