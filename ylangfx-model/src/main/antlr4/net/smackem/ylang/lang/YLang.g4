grammar YLang;

program
    : statement* EOF
    ;

statement
    : (assignStmt
    | pixelStmt
    | invocationStmt
    | ifStmt
    | forStmt
    | whileStmt
    | logStmt
    ) ';'?
    ;

assignStmt
    : Ident Decleq expr
    | Ident Beq expr
    | atom atomSuffix+ Beq expr
    ;

pixelStmt
    : At expr Beq expr
    ;

invocationStmt
    : atom atomSuffix* invocationSuffix
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

logStmt
    : 'log' '(' arguments ')'
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
    | At
    ;

atomSuffix
    : memberSuffix
    | indexSuffix
    | invocationSuffix
    ;

memberSuffix
    : Dot Ident
    ;

indexSuffix
    : '[' (expr | range) ']'
    ;

invocationSuffix
    : '(' arguments? ')'
    ;

atom
    : number
    | Ident
    | String
    | Color
    | True
    | False
    | Nil
    | kernel
    | map
    | list
    | '(' expr ')'
    ;

kernel
    : '|' number+ '|'
    ;

map
    : '{' mapEntries? '}'
    ;

mapEntries
    : mapEntry (',' mapEntry)*
    ;

mapEntry
    : (Ident | String) ':' expr
    ;

list
    : '[' arguments? ']'
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
Decleq  : ':=';
Concat  : '::';
Pair    : ';';
In      : 'in';
Not     : 'not';
Dot     : '.';
At      : '@';
True    : 'true';
False   : 'false';
Nil     : 'nil';

number
    : ('+' | '-')? Number
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*
    ;

Number
    : [0-9]+ ('.' [0-9]+)?
    ;

Color
    : '#' HexLiteral+ (':' HexLiteral HexLiteral)?
    ;

HexLiteral
    : ('a' .. 'f' | 'A' .. 'F' | '0' .. '9')
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
