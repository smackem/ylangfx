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
    : comparison (conditionOp comparison)?
    ;

conditionOp
    : Or
    | And
    ;

comparison
    : tuple (comparator tuple)*
    ;

comparator
    : Eq
    | Lt
    | Le
    | Gt
    | Ge
    | Ne
    | In
    ;

tuple
    : term (Pair term)?
    ;

term
    : product (termOp product)*
    ;

termOp
    : Plus
    | Minus
    | Concat
    ;

product
    : molecule (productOp molecule)*
    ;

productOp
    : Times
    | Div
    | Mod
    ;

molecule
    : atomPrefix? atom atomSuffix*
    ;

atomPrefix
    : Minus
    | Not
    ;

atomSuffix
    : Dot Ident
    | '[' expr ']'
    | '[' range ']'
    ;

atom
    : number
    | Ident
    | String
    | '(' expr ')'
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
Concat  : '::';
Pair    : ';';
In      : 'in';
Not     : 'not';
Dot     : '.';

number
    : ('+' | '-')? Number
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*
    ;

Number
    : [0-9]+ ('.' [0-9]+)?
    ;

String
    : '"' ~["\\\r\n]* '"'
    ;

Comment
    : '//' ~ [\r\n]* -> skip
    ;

Ws
    : [ \t\r\n] -> skip
    ;
