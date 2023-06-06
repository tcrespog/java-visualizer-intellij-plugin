package com.aegamesi.java_visualizer.model;

import org.json.JSONArray;

import java.util.Map;
import java.util.Objects;

public class Value {
	// primitive or reference
	public Type type;
	public long longValue;
	public double doubleValue;
	public boolean booleanValue;
	public String stringValue;
	public char charValue;
	public long reference;

	public boolean changed;

	public enum Type {
		NULL, VOID, LONG, DOUBLE, BOOLEAN, STRING, CHAR, REFERENCE;
	}

	@Override
	public String toString() {
		switch (type) {
			case NULL:
				return "null";
			case STRING:
				return "\"" + stringValue + "\"";
			case LONG:
				return Long.toString(longValue);
			case DOUBLE:
				return Double.toString(doubleValue);
			case BOOLEAN:
				return Boolean.toString(booleanValue);
			case CHAR:
				return "'" + charValue + "'";
			case REFERENCE:
				return "*REF*";
			default:
				return "<?>";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Value value = (Value) o;

		if (longValue != value.longValue) return false;
		if (Double.compare(value.doubleValue, doubleValue) != 0) return false;
		if (booleanValue != value.booleanValue) return false;
		if (charValue != value.charValue) return false;
		if (reference != value.reference) return false;
		if (type != value.type) return false;
		return Objects.equals(stringValue, value.stringValue);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = type.hashCode();
		result = 31 * result + (int) (longValue ^ (longValue >>> 32));
		temp = Double.doubleToLongBits(doubleValue);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (booleanValue ? 1 : 0);
		result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
		result = 31 * result + (int) charValue;
		result = 31 * result + (int) (reference ^ (reference >>> 32));
		return result;
	}

	JSONArray toJson() {
		JSONArray a = new JSONArray();
		a.put(type.name());
		switch (type) {
			case STRING:
				a.put(stringValue);
				break;
			case LONG:
				a.put(longValue);
				break;
			case DOUBLE:
				a.put(doubleValue);
				break;
			case BOOLEAN:
				a.put(booleanValue);
				break;
			case CHAR:
				a.put(charValue);
				break;
			case REFERENCE:
				a.put(reference);
				break;
		}
		return a;
	}

	static Value fromJson(JSONArray a) {
		Value v = new Value();
		v.type = Type.valueOf(a.getString(0));
		switch (v.type) {
			case STRING:
				v.stringValue = a.getString(1);
				break;
			case LONG:
				v.longValue = a.getLong(1);
				break;
			case DOUBLE:
				v.doubleValue = a.getDouble(1);
				break;
			case BOOLEAN:
				v.booleanValue = a.getBoolean(1);
				break;
			case CHAR:
				v.charValue = (char) a.getInt(1);
				break;
			case REFERENCE:
				v.reference = a.getLong(1);
				break;
		}
		return v;
	}

	public static void annotateDiffInVars(Map<String, Value> targetVars, Map<String, Value> compareVars) {
		for (Map.Entry<String, Value> targetVar : targetVars.entrySet()) {

			Value comparisonValue = compareVars.get(targetVar.getKey());
			boolean varAlreadyExisted = (comparisonValue != null);
			if (!varAlreadyExisted)
				continue;

			Value targetValue = targetVar.getValue();
			boolean isEqual = targetValue.equals(comparisonValue);
			targetValue.changed = !isEqual;
		}
	}
}
