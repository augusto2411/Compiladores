package com.uepb.semantic;

public class Symbol {
    private String name;
    private String type;  // Pode ser "number", "string", etc.
    private Object value; // Pode ser null se ainda não tiver valor

    public Symbol(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', type='" + type + "', value=" + value + '}';
    }
}
