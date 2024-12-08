package name.hendrik_scholz.mq.adapter.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    BINARY("binary"),
    TEXT("text");

    private final String label;

    private static final Map<String, MessageType> messageTypeMap = new HashMap<>();

    static {
        for (MessageType messageType : values()) {
            messageTypeMap.put(messageType.label, messageType);
        }
    }

    MessageType(String label) {
        this.label = label;
    }

    public static MessageType valueOfLabel(String label) {
        return messageTypeMap.get(label);
    }

    @Override
    public String toString() {
        return this.label;
    }
}
