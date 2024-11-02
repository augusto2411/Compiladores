package com.uepb.semantic;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private Map<String, Symbol> symbols;
    private Scope parent;

    public Scope() {
        this.symbols = new HashMap<>();
        this.parent = null;
    }

    public Scope(Scope parent) {
        this.symbols = new HashMap<>();
        this.parent = parent;
    }

    public void define(String name, Object value) {
        symbols.put(name, new Symbol(name, "undefined", value));
    }

    public boolean isDefined(String name) {
        return symbols.containsKey(name) || (parent != null && parent.isDefined(name));
    }

    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else if (parent != null) {
            return parent.get(name);
        }
        return null;
    }

    public Scope getParent() {
        return parent; // Método para acessar o escopo pai
    }
}
