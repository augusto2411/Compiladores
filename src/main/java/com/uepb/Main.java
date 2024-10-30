package com.uepb;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Carrega o código de um arquivo (ou pode ser modificado para ler diretamente de uma string)
            CharStream code = CharStreams.fromFileName("teste");
            
            // Instancia o lexer e o parser
            UEPBLanguageLexer lexer = new UEPBLanguageLexer(code);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UEPBLanguageParser parser = new UEPBLanguageParser(tokens);

            // Executa o parser a partir da regra 'program'
            ParseTree tree = parser.program();

            // Exibe a árvore de análise (opcional)
            System.out.println(tree.toStringTree(parser));

            // Se desejar, aqui você pode processar a árvore ou usar um Listener/Visitor para interpretar o código
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}