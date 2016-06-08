package core.data.serializer;

import com.activeandroid.serializer.TypeSerializer;

final public class IntegerArraySerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return int[].class;
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
        return toString((int[]) data);
    }

    @Override
    public int[] deserialize(Object data) {
        if (data == null) {
            return null;
        }
        return toArray((String) data);
    }

    private int[] toArray(String value) {
        String[] values = value.split(",");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    private String toString(int[] values) {
        String result = "";
        for (int i = 0; i < values.length; i++) {
            result += String.valueOf(values[i]);
            if (i < values.length - 1) {
                result += ",";
            }
        }
        return result;
    }
}