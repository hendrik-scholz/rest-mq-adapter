package name.hendrik_scholz.mq.adapter.enums;

import java.util.HashMap;

public enum HeaderName {
    MESSAGE_TYPE("message-type"),
    ENCODING("encoding"),
    CCSID("ccsid"),
    CORRELATION_ID("correlation-id");

    private final String label;

    private static final HashMap<String, HeaderName> headerMap = new HashMap<>();

    static {
        for (HeaderName headerName : values()) {
            headerMap.put(headerName.label, headerName);
        }
    }

    HeaderName(String label) {
        this.label = label;
    }

    public static HeaderName valueOfLabel(String label) {
        return headerMap.get(label);
    }

    @Override
    public String toString() {
        return this.label;
    }
}
