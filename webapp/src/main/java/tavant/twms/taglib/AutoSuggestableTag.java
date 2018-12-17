package tavant.twms.taglib;

/**
 * @see AutoSuggestable
 *
 * @author janmejay.singh
 */
public abstract class AutoSuggestableTag extends InputComponentTag {
    
    private String autoSuggest;
    private String url;
    private String searchDelay;
    private String maxListLength;
    private String autoComplete;
    private String associatedFieldIds;

    protected void populateParams() {
        super.populateParams();
        AutoSuggestable field = (AutoSuggestable) component;
        field.setAutoSuggest(autoSuggest);
        field.setUrl(url);
        field.setSearchDelay(searchDelay);
        field.setMaxListLength(maxListLength);
        field.setAutoComplete(autoComplete);
        if(associatedFieldIds != null) {
            field.setAssociatedFieldIds(associatedFieldIds);
        }
    }

    public String getAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    public String getAutoSuggest() {
        return autoSuggest;
    }

    public void setAutoSuggest(String autoSuggest) {
        this.autoSuggest = autoSuggest;
    }

    public String getMaxListLength() {
        return maxListLength;
    }

    public void setMaxListLength(String maxListLength) {
        this.maxListLength = maxListLength;
    }

    public String getSearchDelay() {
        return searchDelay;
    }

    public void setSearchDelay(String searchDelay) {
        this.searchDelay = searchDelay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssociatedFieldIds() {
        return associatedFieldIds;
    }

    public void setAssociatedFieldIds(String associatedFieldIds) {
        this.associatedFieldIds = associatedFieldIds;
    }
}
