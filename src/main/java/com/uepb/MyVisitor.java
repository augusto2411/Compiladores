package com.uepb;

import java.util.ArrayList;
import java.util.List;

import com.uepb.semantic.Semantic;

public class MyVisitor extends UEPBLanguageBaseVisitor<Void> {
    private Semantic semanticAnalyzer;
    private PCodeGenerator pCodeGenerator;
    private List<String> errors;

    public MyVisitor(Semantic semanticAnalyzer, PCodeGenerator pCodeGenerator) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.pCodeGenerator = pCodeGenerator;
        this.errors = new ArrayList<>();
    }

    public void addStopInstruction() {
        pCodeGenerator.addInstruction("stp"); // Adiciona a instrução de parada
    }

    // Outros métodos de visita (visitVarDeclaration, visitAssignment, etc.)

    @Override
    public Void visitProgram(UEPBLanguageParser.ProgramContext ctx) {
        // Percorre todas as declarações no programa
        for (UEPBLanguageParser.StatementContext statement : ctx.statement()) {
            visit(statement);
        }
        
        // Adiciona a instrução 'stp' no final do programa
        addStopInstruction();
        
        return null;
    }
    

    @Override
    public Void visitVarDeclaration(UEPBLanguageParser.VarDeclarationContext ctx) {
        String varName = ctx.IDENT().getText();
        semanticAnalyzer.checkVarDeclaration(varName);
        
        int address = pCodeGenerator.allocateMemory(varName);
        pCodeGenerator.addInstruction("lda #" + address); 

        if (ctx.expression() != null) {
            visit(ctx.expression()); // Avalia a expressão
            // Adicione lógica para obter e armazenar o valor da expressão se necessário
            pCodeGenerator.addInstruction("sto ");
        }
       
        return null;
    }

    @Override
    public Void visitAssignment(UEPBLanguageParser.AssignmentContext ctx) {
        String varName = ctx.IDENT().getText();
        int address = pCodeGenerator.getMemoryAddress(varName); // Obtém o endereço da variável
    
        // 1. Carregar o endereço da variável na pilha
        pCodeGenerator.addInstruction("lda #" + address); // Insere o endereço na pilha
    
        // 2. Avaliar a expressão do lado direito
        visit(ctx.expression()); // Avalia a expressão que deve ser armazenada
    
        // 3. Armazenar o valor no endereço da variável
        pCodeGenerator.addInstruction("sto"); // Armazena o valor no endereço
    
        return null;
    }

    @Override
    public Void visitIfStatement(UEPBLanguageParser.IfStatementContext ctx) {
        String endLabel = "endIf"; // Rótulo para o fim do if
    
        // Avalia a condição e gera o PCode correspondente
        visit(ctx.condition());
    
        // Se a condição for falsa, salta para o final do if
        pCodeGenerator.addInstruction("fjp " + endLabel);
    
        // Executa as instruções dentro do bloco if
        for (UEPBLanguageParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
    
        // Rótulo para o final do if
        pCodeGenerator.addInstruction(endLabel + ":");
        return null;
    }
    

    @Override
    public Void visitCondition(UEPBLanguageParser.ConditionContext ctx) {
        visit(ctx.logicalExpression()); // Avalia a expressão lógica
    
        // Após visitar a expressão lógica, não é necessário fazer nada aqui,
        // pois a condição é avaliada diretamente na instrução do while.
    
        return null;
    }
    @Override
    public Void visitLogicalExpression(UEPBLanguageParser.LogicalExpressionContext ctx) {
        // Avalia a primeira comparação
        visit(ctx.comparison(0)); // O resultado de ctx.comparison(0) deve estar na pilha
    
        // Processa as comparações subsequentes
        for (int i = 1; i < ctx.comparison().size(); i++) {
            visit(ctx.comparison(i)); // Avalia a próxima comparação
            String operator = ctx.getChild((i * 2) - 1).getText(); // Obtém o operador
    
            // A operação lógica deve ser feita após garantir que ambos os valores estão na pilha
            if (operator.equals("and")) {
                pCodeGenerator.addInstruction("and"); // Verifica se há dois operandos booleanos na pilha
            } else if (operator.equals("or")) {
                pCodeGenerator.addInstruction("or"); // Verifica se há dois operandos booleanos na pilha
            }
        }
        
        return null;
    }

    public Void visitComparison(UEPBLanguageParser.ComparisonContext ctx) {
        // Exemplo simplificado para lidar com comparações
        if (ctx.children.size() == 3) {
            visit(ctx.children.get(0)); // Avalia o operando da esquerda
            visit(ctx.children.get(2)); // Avalia o operando da direita
            String operator = ctx.children.get(1).getText(); // Obtém o operador (>, <, ==, etc.)
    
            // Adiciona a instrução correspondente ao PCode
            switch (operator) {
                case "<":
                    pCodeGenerator.addInstruction("let"); // Adiciona a comparação para "menor que"
                    break;
                case ">":
                    pCodeGenerator.addInstruction("grt"); // Adiciona a comparação para "maior que"
                    break;
                case "==":
                    pCodeGenerator.addInstruction("equ"); // Adiciona a comparação para "igual a"
                    break;
            }
        } else if (ctx.children.size() == 1) {
            // Caso onde o filho único é um booleano
            String boolValue = ctx.getText(); // "true" ou "false"
            if (boolValue.equals("true")) {
                pCodeGenerator.addInstruction("ldc true"); // Insere true na pilha
            } else if (boolValue.equals("false")) {
                pCodeGenerator.addInstruction("ldc false"); // Insere false na pilha
            }
        }
    
        return null;
    }
    
    
    @Override
    public Void visitWhileStatement(UEPBLanguageParser.WhileStatementContext ctx) {
        // Entrando em um novo escopo semântico
        semanticAnalyzer.enterScope();
    
        // Criar rótulos para o início e o final do loop
        String loopStartLabel = "loopStart";
        String loopEndLabel = "loopEnd";
    
        // Adiciona o rótulo do início do loop
        pCodeGenerator.addInstruction(loopStartLabel + ":");
    
        // Avalia a condição do while
        visit(ctx.condition());
    
        // A condição deve resultar em um booleano na pilha
        pCodeGenerator.addInstruction("fjp " + loopEndLabel); // Salta para o final se a condição for falsa
    
        // Visita cada declaração dentro do bloco while
        for (UEPBLanguageParser.StatementContext statementContext : ctx.statement()) {
            visit(statementContext);
        }
    
        // Adiciona instrução para voltar ao início do loop e reavaliar a condição
        pCodeGenerator.addInstruction("ujp " + loopStartLabel); // Salta de volta para reavaliar a condição
    
        // Adiciona o rótulo do final do loop
        pCodeGenerator.addInstruction(loopEndLabel + ":");
    
        // Saindo do escopo semântico
        semanticAnalyzer.exitScope();
        return null;
    }
    

    @Override
    public Void visitPrintStatement(UEPBLanguageParser.PrintStatementContext ctx) {
        if (ctx.STRING() != null) {
            String stringValue = ctx.STRING().getText();
            stringValue = stringValue.substring(1, stringValue.length() - 1); 
            pCodeGenerator.addInstruction("ldc \"" + stringValue + "\"");
            pCodeGenerator.addInstruction("wri");
        } else {
            visit(ctx.expression());
            pCodeGenerator.addInstruction("wri"); // Assume que o valor da expressão foi carregado na pilha
        }
    
        // Adiciona nova linha após imprimir
        pCodeGenerator.addInstruction("ldc \"\\n\""); // Carrega nova linha
        pCodeGenerator.addInstruction("wri"); // Imprime nova linha
    
        return null;
    }
    

    @Override
    public Void visitInputStatement(UEPBLanguageParser.InputStatementContext ctx) {
        String varName = ctx.IDENT().getText();
        if (!semanticAnalyzer.isVariableDeclared(varName)) {
            semanticAnalyzer.checkVarDeclaration(varName);
        }
        int address = pCodeGenerator.getMemoryAddress(varName);
        pCodeGenerator.addInstruction("lda #" + address); // Carrega o endereço da variável
        pCodeGenerator.addInstruction("rd"); // Lê valor da entrada
       
        pCodeGenerator.addInstruction("sto"); // Armazena o valor no endereço
        return null;
    }

    @Override
    public Void visitExpression(UEPBLanguageParser.ExpressionContext ctx) {
        if (ctx.additiveExpression() != null) {
            visit(ctx.additiveExpression());
        }
        return null;
    }

    @Override
    public Void visitAdditiveExpression(UEPBLanguageParser.AdditiveExpressionContext ctx) {
        visit(ctx.multiplicativeExpression(0));
        
        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            visit(ctx.multiplicativeExpression(i));
            String operator = ctx.getChild((i * 2) - 1).getText();
            if (operator.equals("+")) {
                pCodeGenerator.addInstruction("add");
            } else if (operator.equals("-")) {
                pCodeGenerator.addInstruction("sub");
            }
        }
        return null;
    }

    @Override
    public Void visitMultiplicativeExpression(UEPBLanguageParser.MultiplicativeExpressionContext ctx) {
        visit(ctx.exponentiationExpression(0));
        for (int i = 1; i < ctx.exponentiationExpression().size(); i++) {
            visit(ctx.exponentiationExpression(i));
            String operator = ctx.getChild((i * 2) - 1).getText();
            if (operator.equals("*")) {
                pCodeGenerator.addInstruction("mul");
            } else {
                pCodeGenerator.addInstruction("div");
            }
        }
        return null;
    }

    @Override
    public Void visitExponentiationExpression(UEPBLanguageParser.ExponentiationExpressionContext ctx) {
        visit(ctx.unaryExpression(0));
        for (int i = 1; i < ctx.unaryExpression().size(); i++) {
            visit(ctx.unaryExpression(i));
            pCodeGenerator.addInstruction("exp");
        }
        return null;
    }

    @Override
    public Void visitUnaryExpression(UEPBLanguageParser.UnaryExpressionContext ctx) {
        if (ctx.IDENT() != null) {
            String varName = ctx.IDENT().getText();
            
            // Verifica se a variável foi declarada antes de usá-la
            if (!semanticAnalyzer.isVariableDeclared(varName)) {
                errors.add("Erro: A variável '" + varName + "' não foi declarada.");
                return null; // Retorna imediatamente para evitar continuar com a geração de PCode
            }
            
            int address = pCodeGenerator.getMemoryAddress(varName);
            pCodeGenerator.addInstruction("lod #" + address); // Carrega o valor da variável na pilha
        } else if (ctx.NUMBER() != null) {
            pCodeGenerator.addInstruction("ldc " + ctx.NUMBER().getText()); // Carrega o número constante
        } else if (ctx.expression() != null) {
            visit(ctx.expression()); // Visita expressões aninhadas
        } else if (ctx.getChild(0).getText().equals("-")) {
            visit(ctx.unaryExpression()); // Chama a expressão unária corretamente
            pCodeGenerator.addInstruction("neg"); // Aplica negação
        }
        return null;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}