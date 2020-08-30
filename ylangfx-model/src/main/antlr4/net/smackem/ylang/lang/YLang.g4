grammar YLang;

program
    : statement* EOF
    ;

statement
    : (identStmt
    | pixelStmt
    | ifStmt
    | forStmt
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

expr
    : '@'? (number
    | Ident)
    ;

arguments
    : expr (',' expr)*
    ;

forStmt
    : 'for' Ident 'in' expr block
    | 'for' Ident 'in' range expr block
    ;

range
    : number '..' number ('..' number)?
    ;

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
