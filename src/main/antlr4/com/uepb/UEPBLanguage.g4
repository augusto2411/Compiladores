grammar UEPBLanguage;

// Definindo os tokens
VAR     : 'var';
ASSIGN  : '=';
PRINT   : 'print';
INPUT   : 'input';
IF      : 'if';
WHILE   : 'while';
AND     : 'and';
OR      : 'or';
LT      : '<';
GT      : '>';
EQ      : '==';
NUMBER  : [0-9]+;
TRUE    : 'true';
FALSE   : 'false'; 
IDENT   : [a-zA-Z_][a-zA-Z0-9_]*;
STRING  : '"' (~["])* '"'; // String entre aspas
WS      : [ \t\r\n]+ -> skip; // Ignorar espaços em branco

// Entrada do programa
program: statement*;

// Declaração de variáveis
statement: varDeclaration
         | assignment
         | ifStatement
         | whileStatement
         | printStatement
         | inputStatement
         ;

// Declaração de variáveis
varDeclaration: VAR IDENT ('=' expression)? ';';

// Atribuição
assignment: IDENT ASSIGN expression ';';

// Estruturas de controle
ifStatement: IF '(' condition ')' '{' statement* '}';

// Estruturas de repetição
whileStatement: WHILE '(' condition ')' '{' statement* '}';

// Operações de entrada e saída
printStatement: PRINT '(' (STRING | expression) ')' ';';
inputStatement: INPUT '(' IDENT ')' ';';

// Expressões
expression: additiveExpression
           | booleanExpression;

// Expressões aditivas
additiveExpression: multiplicativeExpression (('+' | '-') multiplicativeExpression)*;

// Expressões multiplicativas
multiplicativeExpression: exponentiationExpression (('*' | '/') exponentiationExpression)*;

// Expressões de exponenciação
exponentiationExpression: unaryExpression (('^' unaryExpression)*);

// Expressões unárias
unaryExpression: IDENT | NUMBER | '(' expression ')' | ('-' unaryExpression);

// Condições
condition: logicalExpression;

// Expressões lógicas
logicalExpression: comparison (('and' | 'or') comparison)*;

// Comparações
comparison: additiveExpression (LT | GT | EQ) additiveExpression
           | IDENT // Permitir identificação direta
           | NUMBER // Permitir comparação com número
           | '(' logicalExpression ')' // Permitir aninhamento de expressões lógicas
           | booleanExpression; // Permitir inclusão de booleanos nas comparações

// Expressões booleanas
booleanExpression: TRUE | FALSE;
