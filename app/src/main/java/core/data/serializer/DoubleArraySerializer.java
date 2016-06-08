package core.data.serializer;

import com.activeandroid.serializer.TypeSerializer;

final public class DoubleArraySerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return double[].class;
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
        return toString((double[]) data);
    }

    @Override
    public double[] deserialize(Object data) {
        if (data == null) {
            return null;
        }
        return toArray((String) data);
    }

    private double[] toArray(String value) {
        String[] values = value.split("|");
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Double.parseDouble(values[i]);
        }
        return result;
    }

    private String toString(double[] values) {
        String result = "";
        for (int i = 0; i < values.length; i++) {
            result += String.valueOf(values[i]);
            if (i < values.length - 1) {
                result += "|";
            }
        }
        return result;
    }
}