package com.uepb.semantic;

public class Semantic {
    private Scope currentScope;

    public Semantic() {
        currentScope = new Scope(); // Cria o escopo inicial
    }

    // Verifica uma declaração de variável
    public void checkVarDeclaration(String name) {
        if (currentScope.isDefined(name)) {
            throw new RuntimeException("Erro: A variável '" + name + "' já foi declarada.");
        }
        currentScope.define(name, null);
    }

    // Verifica uma atribuição de variável
    public void checkAssignment(String name, Object value) {
        if (!currentScope.isDefined(name)) {
            throw new RuntimeException("Erro: A variável '" + name + "' não foi declarada.");
        }
        currentScope.get(name).setValue(value); // Atualiza o valor da variável
    }
    
    public boolean isVariableDeclared(String name) {
        return currentScope.isDefined(name);
    }

    // Entra em um novo escopo
    public void enterScope() {
        currentScope = new Scope(currentScope); // Cria um novo escopo com o atual como pai
    }

    // Sai do escopo atual
    public void exitScope() {
        if (currentScope.getParent() != null) { // Garante que há um escopo pai
            currentScope = currentScope.getParent(); // Retorna ao escopo pai
        }
    }

    public Scope getCurrentScope() {
        return currentScope;
    }
}
