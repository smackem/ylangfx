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
    )? LineBreak
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
    : LBrace statement* RBrace
    ;

arguments
    : expr (Comma expr)*
    ;

forStmt
    : 'for' Ident 'in' expr whereClause? block
    ;

whereClause
    : 'where' expr
    ;

whileStmt
    : 'while' expr block
    ;

logStmt
    : 'log' LParen arguments RParen
    ;

swapStmt
    : Ident Swap Ident
    ;

expr
    : condition (QMark term Colon expr)?
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
    : LBracket expr RBracket
    ;

invocationSuffix
    : LParen arguments? RParen
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
    | LParen expr RParen
    ;

functionInvocation
    : Ident invocationSuffix
    ;

kernel
    : Pipe (number LineBreak?)+ Pipe
    ;

map
    : LBrace mapEntries? Comma? RBrace
    ;

mapEntries
    : mapEntry (Comma mapEntry)*
    ;

mapEntry
    : (Ident | String) Colon expr
    ;

list
    : LBracket arguments? Comma? RBracket
    ;

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
Concat  : '::';
Pair    : ';';
In      : 'in';
Not     : 'not';
At      : '@';
True    : 'true';
False   : 'false';
Nil     : 'nil';
FromTo  : '..';
Swap    : '<=>';
Pipe    : '|';

Or          : 'or' LineBreak?;
And         : 'and' LineBreak?;
Beq         : '=' LineBreak?;
Decleq      : ':=' LineBreak?;
QMark       : '?' LineBreak?;
Colon       : ':' LineBreak?;
Dot         : '.' LineBreak?;
Comma       : ',' LineBreak?;
LBrace      : '{' LineBreak?;
RBrace      : '}';
LBracket    : '[' LineBreak?;
RBracket    : ']';
LParen      : '(' LineBreak?;
RParen      : ')';

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

LineBreak
    : [\r\n]+
    ;

Ws
    : [ \t] -> skip
    ;
