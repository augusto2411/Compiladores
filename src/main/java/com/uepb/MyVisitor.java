package com.uepb;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import com.uepb.semantic.Semantic;
import com.uepb.semantic.Scope;

public class MyVisitor extends UEPBLanguageBaseVisitor<Void> {
    private Semantic semanticAnalyzer;
    private PCodeGenerator pCodeGenerator;
    private List<String> errors;
    
    public MyVisitor(Semantic semanticAnalyzer, PCodeGenerator pCodeGenerator) {
        this.semanticAnalyzer = semanticAnalyzer;
        this.pCodeGenerator = pCodeGenerator;
        this.errors = new ArrayList<>();
    }

    @Override
    public Void visitVarDeclaration(UEPBLanguageParser.VarDeclarationContext ctx) {
        String varName = ctx.IDENT().getText();
        semanticAnalyzer.checkVarDeclaration(varName);
        if (ctx.expression() != null) {
            Object value = visit(ctx.expression());
            semanticAnalyzer.checkAssignment(varName, value);
            pCodeGenerator.addInstruction("CREATE " + varName + " " + value);
        }
        return null;
    }

    @Override
    public Void visitAssignment(UEPBLanguageParser.AssignmentContext ctx) {
        String varName = ctx.IDENT().getText();
        Object value = visit(ctx.expression());
        semanticAnalyzer.checkAssignment(varName, value);
        pCodeGenerator.addInstruction("ASSIGN " + varName + " " + value);
        return null;
    }

    @Override
    public Void visitIfStatement(UEPBLanguageParser.IfStatementContext ctx) {
        // Entrar em um novo escopo
        semanticAnalyzer.enterScope(); // Chama apenas para alterar o escopo atual

        // Visitar a condição
        visit(ctx.condition());

        // Visitar o bloco de declarações
        for (UEPBLanguageParser.StatementContext statementContext : ctx.statement()) {
            visit(statementContext);
        }

        // Sair do escopo após processar o bloco if
        semanticAnalyzer.exitScope();
        return null;
    }

    @Override
    public Void visitWhileStatement(UEPBLanguageParser.WhileStatementContext ctx) {
        // Entrar em um novo escopo
        semanticAnalyzer.enterScope();

        // Visitar a condição
        visit(ctx.condition());

        // Visitar o bloco de declarações
        for (UEPBLanguageParser.StatementContext statementContext : ctx.statement()) {
            visit(statementContext);
        }

        // Sair do escopo após processar o bloco while
        semanticAnalyzer.exitScope();
        return null;
    }

    @Override
    public Void visitPrintStatement(UEPBLanguageParser.PrintStatementContext ctx) {
        String output;
        
        // Verifique se o contexto da STRING não é nulo
        if (ctx.STRING() != null) {
            output = ctx.STRING().getText();
        } else {
            // Aqui você deve verificar se o resultado da expressão é nulo
            Object expressionResult = visit(ctx.expression());
            output = (expressionResult != null) ? expressionResult.toString() : "null";
        }
        
        pCodeGenerator.addInstruction("PRINT " + output);
        return null;
    }

    @Override
    public Void visitInputStatement(UEPBLanguageParser.InputStatementContext ctx) {
        String varName = ctx.IDENT().getText();
        
        // Apenas verificar se a variável foi declarada antes de chamar input
        if (!semanticAnalyzer.isVariableDeclared(varName)) {
            semanticAnalyzer.checkVarDeclaration(varName);
        }

        pCodeGenerator.addInstruction("INPUT " + varName);
        return null;
    }

    @Override
    public Void visitExpression(UEPBLanguageParser.ExpressionContext ctx) {
        // Processar as expressões
        return null;
    }
    public boolean hasErrors() {
        return !errors.isEmpty(); // Retorna true se houver erros
    }
}

