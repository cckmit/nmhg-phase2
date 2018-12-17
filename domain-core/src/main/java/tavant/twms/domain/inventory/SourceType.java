package tavant.twms.domain.inventory;

public enum SourceType {
    CLAIM("Claim"), TELEMETRY("Telemetry"), INTERNAL_UPLOAD("Internal Upload"), EXTERNAL_UPLOAD("External Upload");

    private String source;

    private SourceType(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }
     
    @Override
    public String toString() {
        return this.source;
    }
}
