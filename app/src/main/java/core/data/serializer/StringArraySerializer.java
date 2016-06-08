package core.data.serializer;

import com.activeandroid.serializer.TypeSerializer;

@SuppressWarnings({"WeakerAccess", "unused"})
final public class StringArraySerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return String[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object data) {
        if (data == null) {
            return null;
        }
        return toString((String[]) data);
    }

    @Override
    public String[] deserialize(Object data) {
        if (data == null) {
            return null;
        }
        return toArray((String) data);
    }

    private String[] toArray(String value) {
        return value.split("|");
    }

    private String toString(String[] values) {
        String result = "";
        for (int i = 0; i < values.length; i++) {
            result += values[i];
            if (i < values.length - 1) {
                result += "|";
            }
        }
        return result;
    }
}
