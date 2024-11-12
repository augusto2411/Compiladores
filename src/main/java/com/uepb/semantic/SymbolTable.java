package com.uepb.semantic;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> symbols;

    public SymbolTable() {
        symbols = new HashMap<>();
    }

    public void define(String name, String type, Object value) {
        symbols.put(name, new Symbol(name, type, value));
    }

    public Symbol get(String name) {
        return symbols.get(name);
    }

    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }
}