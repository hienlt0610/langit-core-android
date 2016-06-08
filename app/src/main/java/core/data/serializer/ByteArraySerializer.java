package core.data.serializer;

import com.activeandroid.serializer.TypeSerializer;

final public class ByteArraySerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return byte[].class;
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
        return toString((byte[]) data);
    }

    @Override
    public byte[] deserialize(Object data) {
        if (data == null) {
            return null;
        }
        return toArray((String) data);
    }

    private byte[] toArray(String value) {
        String[] values = value.split(",");
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Byte.parseByte(values[i]);
        }
        return result;
    }

    private String toString(byte[] values) {
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