grammar YLang;

program
    : statement* EOF
    ;

statement
    : (identStmt
    | pixelStmt
    | ifStmt
    | forStmt
    | whileStmt
    ) ';'?
    ;

identStmt
    : Ident ':=' expr
    | Ident '=' expr
    | Ident '[' expr ']' '=' expr
    | Ident '(' arguments? ')'
    ;

pixelStmt
    : '@' expr '=' expr
    ;

ifStmt
    : 'if' expr block elseIfClause* elseClause?
    ;

elseIfClause
    : 'else' 'if' expr block
    ;

elseClause
    : 'else' block
    ;

block
    : '{' statement* '}'
    ;

arguments
    : expr (',' expr)*
    ;

forStmt
    : 'for' Ident 'in' expr block
    | 'for' Ident 'in' range block
    ;

range
    : number '..' number ('..' number)?
    ;

whileStmt
    : 'while' expr block
    ;

expr
    : condition ('?' term ':' expr)?
    ;

condition
    : comparison (conditionOperator comparison)?
    ;

conditionOperator
    : Or
    | And
    ;

comparison
    : term (comparator term)*
    ;

comparator
    : Eq
    | Lt
    | Le
    | Gt
    | Ge
    | Ne
    ;

term
    : atom (termOperator atom)*
    ;

termOperator
    : Plus
    | Minus
    ;

atom
    : Number
    | Ident
    ;

Or      : 'or';
And     : 'and';
Plus    : '+';
Minus   : '-';
Times   : '*';
Div     : '/';
Mod     : '%';
Lt      : '<';
Le      : '<=';
Gt      : '>';
Ge      : '>=';
Eq      : '==';
Ne      : '!=';
Beq     : '=';

number
    : ('+' | '-')? Number
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*
    ;

Number
    : [0-9]+ ('.' [0-9]+)?
    ;

Comment
    : '//' ~ [\r\n]* -> skip
    ;

Ws
    : [ \t\r\n] -> skip
    ;
