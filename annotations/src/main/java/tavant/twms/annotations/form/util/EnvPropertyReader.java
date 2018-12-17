package tavant.twms.annotations.form.util;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 1:45:41 PM
 */
public interface EnvPropertyReader {
    
    public static final String OUT_FILE = "outFile",
                               DTD_PATH = "dtdPath",
                               CREATE_REQUEST_VIEW_WRAPPER = "createRequestViewWrapper",
                               CREATE_REQUEST_INPUT_VIEW_WRAPPER = "createRequestInputViewWrapper",
                               CREATE_INPUT_VIEW_WRAPPER = "createInputViewWrapper",
                               CREATE_SUCCESS_VIEW_WRAPPER = "createSuccessViewWrapper",
                               UPDATE_REQUEST_VIEW_WRAPPER = "updateRequestViewWrapper",
                               UPDATE_REQUEST_INPUT_VIEW_WRAPPER = "updateRequestInputViewWrapper",
                               UPDATE_INPUT_VIEW_WRAPPER = "updateInputViewWrapper",
                               UPDATE_SUCCESS_VIEW_WRAPPER = "updateSuccessViewWrapper",
                               VIEW_REQUEST_VIEW_WRAPPER = "viewRequestViewWrapper",
                               VIEW_REQUEST_INPUT_VIEW_WRAPPER = "viewRequestInputViewWrapper",
                               VIEW_INPUT_VIEW_WRAPPER = "viewInputViewWrapper",
                               VIEW_SUCCESS_VIEW_WRAPPER = "viewSuccessViewWrapper",
                               DELETE_REQUEST_VIEW_WRAPPER = "deleteRequestViewWrapper",
                               DELETE_REQUEST_INPUT_VIEW_WRAPPER = "deleteRequestInputViewWrapper",
                               DELETE_INPUT_VIEW_WRAPPER = "deleteInputViewWrapper",
                               DELETE_SUCCESS_VIEW_WRAPPER = "deleteSuccessViewWrapper",
                               READ_REQUEST_VIEW_WRAPPER = "readRequestViewWrapper",
                               READ_REQUEST_INPUT_VIEW_WRAPPER = "readRequestInputViewWrapper",
                               WRAPPER_FILE_DIRECTORY_PATH = "wrapperFileDirectoryPath",
                               PACKAGE_NAME_VALUE = "packageName",
                               PACKAGE_EXTENDS_VALUE = "packageExtends",
                               PACKAGE_ABSTRACT_VALUE = "packageAbstract",
                               PACKAGE_EXTERNAL_REFERENCE_RESOLVER_VALUE = "packageExternalReferenceResolver",
                               PACKAGE_NAMESPACE_VALUE = "packageNamespace";

    public String getProperty(String key);
}
