package com.uepb;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;
import com.uepb.semantic.Semantic;

public class Main {
    public static void main(String[] args) {
        try {
            // Carrega o código de um arquivo
            CharStream code = CharStreams.fromFileName("teste.txt");

            // Instancia o lexer e o parser
            UEPBLanguageLexer lexer = new UEPBLanguageLexer(code);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UEPBLanguageParser parser = new UEPBLanguageParser(tokens);

            // Configura um listener de erro para capturar erros de sintaxe
            parser.removeErrorListeners(); // Remove os ouvintes de erro padrão
            parser.addErrorListener(new ConsoleErrorListener()); // Adiciona um ouvinte personalizado para erros

            // Executa o parser a partir da regra 'program'
            ParseTree tree = parser.program();

            // Verifica se a árvore de parse é nula
            if (tree == null) {
                System.out.println("Erro: A árvore de análise é nula.");
                return;
            }

            // Verifica se há erros de sintaxe
            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.out.println("Erro Sintático.");
                return; // Interrompe a execução se houver erros
            }

            // Exibe a árvore de análise (opcional)
            System.out.println(tree.toStringTree(parser));

            // Cria uma instância da classe Semantic e PCodeGenerator
            Semantic semanticAnalyzer = new Semantic();
            PCodeGenerator pCodeGenerator = new PCodeGenerator();

            // Usa um visitor para percorrer a árvore de parse
            MyVisitor visitor = new MyVisitor(semanticAnalyzer, pCodeGenerator);
            visitor.visit(tree);

            // Verifica se houve erros na análise semântica antes de salvar o PCode
            if (!visitor.hasErrors()) {
                pCodeGenerator.saveToFile("output.pcode");
                System.out.println("Análise semântica completa! PCode gerado em 'output.pcode'.");
            } else {
                System.out.println("Erros encontrados durante a análise semântica. PCode não gerado.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Ouvinte de erro personalizado
class ConsoleErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg,
                            RecognitionException e) {
        System.err.println("Erro de sintaxe em linha " + line + ":" + charPositionInLine + " - " + msg);
    }
}
