grammar YLang;

program
    : statement* returnStmt EOF
    ;

statement
    : (assignStmt
    | invocationStmt
    | ifStmt
    | forStmt
    | whileStmt
    | logStmt
    | swapStmt
    | returnStmt
    ) ';'?
    ;

returnStmt
    : 'return' expr
    ;

assignStmt
    : Ident Decleq expr
    | Ident Beq expr
    | atom atomSuffix+ Beq expr
    ;

invocationStmt
    : Ident invocationSuffix
    | atom atomSuffix+
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
    ;

whileStmt
    : 'while' expr block
    ;

logStmt
    : 'log' '(' arguments ')'
    ;

swapStmt
    : Ident Swap Ident
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
    : tuple (comparator tuple)?
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
    : point
    | range
    | term
    ;

point
    :  term Pair term
    ;

range
    : term FromTo term (FromTo term)?
    ;

term
    : product (termOp product)*
    ;

termOp
    : Plus
    | Minus
    | Concat
    | Cmp
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
    ;

memberSuffix
    : Dot Ident invocationSuffix?
    ;

indexSuffix
    : '[' expr ']'
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
    | EnvironmentArg
    | kernel
    | map
    | list
    | functionInvocation
    | '(' expr ')'
    ;

functionInvocation
    : Ident invocationSuffix
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
Cmp     : '~';
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
FromTo  : '..';
Swap    : '<=>';

number
    : ('+' | '-')? Number
    ;

EnvironmentArg
    : '$' Ident
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*
    ;

Number
    : [0-9]+ ('.' [0-9]+)?
    ;

Color
    : '#' HexLiteral+ ('@' HexLiteral HexLiteral)?
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
