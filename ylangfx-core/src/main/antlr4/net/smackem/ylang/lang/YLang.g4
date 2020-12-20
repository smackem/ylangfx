grammar YLang;

program
    : topLevelStmt* EOF
    ;

topLevelStmt
    : functionDecl
    | optionStmt
    | statement
    ;

functionDecl
    : DocComment? 'fn' Ident LParen parameters? RParen block LineBreak
    ;

parameters
    : Ident (Comma Ident)*
    ;

optionStmt
    : '#' 'option' Ident literal LineBreak
    ;

statement
    : (declStmt
    | assignStmt
    | invocationStmt
    | ifStmt
    | forStmt
    | whileStmt
    | logStmt
    | swapStmt
    | returnStmt
    )? LineBreak
    ;

declStmt
    : DocComment? Ident Decleq expr
    ;

assignStmt
    : Ident Beq expr
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

returnStmt
    : 'return' expr
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
    ;

atomSuffix
    : memberSuffix
    | indexSuffix
    ;

memberSuffix
    : ((Dot Ident) | At) invocationSuffix?
    ;

indexSuffix
    : LBracket expr RBracket
    ;

invocationSuffix
    : LParen arguments? RParen
    ;

atom
    : literal
    | Ident
    | EnvironmentArg
    | map
    | list
    | functionInvocation
    | functionRef
    | LParen expr RParen
    ;

literal
    : number
    | String
    | Color
    | True
    | False
    | Nil
    | kernel
    ;

functionInvocation
    : Ident invocationSuffix
    ;

functionRef
    : At Ident
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

DocCommentLine
    : '///' ~ [\r\n]*
    ;

DocComment
    : (DocCommentLine LineBreak*)+
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
