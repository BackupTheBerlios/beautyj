/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package de.gulden.util.javasource.jjt;

public interface ParserConstants {

  int EOF = 0;
  int SINGLE_LINE_COMMENT = 9;
  int FORMAL_COMMENT = 10;
  int MULTI_LINE_COMMENT = 11;
  int ABSTRACT = 13;
  int ASSERT = 14;
  int BOOLEAN = 15;
  int BREAK = 16;
  int BYTE = 17;
  int CASE = 18;
  int CATCH = 19;
  int CHAR = 20;
  int CLASS = 21;
  int CONST = 22;
  int CONTINUE = 23;
  int _DEFAULT = 24;
  int DO = 25;
  int DOUBLE = 26;
  int ELSE = 27;
  int EXTENDS = 28;
  int FALSE = 29;
  int FINAL = 30;
  int FINALLY = 31;
  int FLOAT = 32;
  int FOR = 33;
  int GOTO = 34;
  int IF = 35;
  int IMPLEMENTS = 36;
  int IMPORT = 37;
  int INSTANCEOF = 38;
  int INT = 39;
  int INTERFACE = 40;
  int LONG = 41;
  int NATIVE = 42;
  int NEW = 43;
  int NULL = 44;
  int PACKAGE = 45;
  int PRIVATE = 46;
  int PROTECTED = 47;
  int PUBLIC = 48;
  int RETURN = 49;
  int SHORT = 50;
  int STATIC = 51;
  int STRICTFP = 52;
  int SUPER = 53;
  int SWITCH = 54;
  int SYNCHRONIZED = 55;
  int THIS = 56;
  int THROW = 57;
  int THROWS = 58;
  int TRANSIENT = 59;
  int TRUE = 60;
  int TRY = 61;
  int VOID = 62;
  int VOLATILE = 63;
  int WHILE = 64;
  int INTEGER_LITERAL = 65;
  int DECIMAL_LITERAL = 66;
  int HEX_LITERAL = 67;
  int OCTAL_LITERAL = 68;
  int FLOATING_POINT_LITERAL = 69;
  int EXPONENT = 70;
  int CHARACTER_LITERAL = 71;
  int STRING_LITERAL = 72;
  int IDENTIFIER = 73;
  int LETTER = 74;
  int DIGIT = 75;
  int LPAREN = 76;
  int RPAREN = 77;
  int LBRACE = 78;
  int RBRACE = 79;
  int LBRACKET = 80;
  int RBRACKET = 81;
  int SEMICOLON = 82;
  int COMMA = 83;
  int DOT = 84;
  int ASSIGN = 85;
  int GT = 86;
  int LT = 87;
  int BANG = 88;
  int TILDE = 89;
  int HOOK = 90;
  int COLON = 91;
  int EQ = 92;
  int LE = 93;
  int GE = 94;
  int NE = 95;
  int SC_OR = 96;
  int SC_AND = 97;
  int INCR = 98;
  int DECR = 99;
  int PLUS = 100;
  int MINUS = 101;
  int STAR = 102;
  int SLASH = 103;
  int BIT_AND = 104;
  int BIT_OR = 105;
  int XOR = 106;
  int REM = 107;
  int LSHIFT = 108;
  int RSIGNEDSHIFT = 109;
  int RUNSIGNEDSHIFT = 110;
  int PLUSASSIGN = 111;
  int MINUSASSIGN = 112;
  int STARASSIGN = 113;
  int SLASHASSIGN = 114;
  int ANDASSIGN = 115;
  int ORASSIGN = 116;
  int XORASSIGN = 117;
  int REMASSIGN = 118;
  int LSHIFTASSIGN = 119;
  int RSIGNEDSHIFTASSIGN = 120;
  int RUNSIGNEDSHIFTASSIGN = 121;

  int DEFAULT = 0;
  int IN_SINGLE_LINE_COMMENT = 1;
  int IN_FORMAL_COMMENT = 2;
  int IN_MULTI_LINE_COMMENT = 3;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "\"//\"",
    "<token of kind 7>",
    "\"/*\"",
    "<SINGLE_LINE_COMMENT>",
    "\"*/\"",
    "\"*/\"",
    "<token of kind 12>",
    "\"abstract\"",
    "\"assert\"",
    "\"boolean\"",
    "\"break\"",
    "\"byte\"",
    "\"case\"",
    "\"catch\"",
    "\"char\"",
    "\"class\"",
    "\"const\"",
    "\"continue\"",
    "\"default\"",
    "\"do\"",
    "\"double\"",
    "\"else\"",
    "\"extends\"",
    "\"false\"",
    "\"final\"",
    "\"finally\"",
    "\"float\"",
    "\"for\"",
    "\"goto\"",
    "\"if\"",
    "\"implements\"",
    "\"import\"",
    "\"instanceof\"",
    "\"int\"",
    "\"interface\"",
    "\"long\"",
    "\"native\"",
    "\"new\"",
    "\"null\"",
    "\"package\"",
    "\"private\"",
    "\"protected\"",
    "\"public\"",
    "\"return\"",
    "\"short\"",
    "\"static\"",
    "\"strictfp\"",
    "\"super\"",
    "\"switch\"",
    "\"synchronized\"",
    "\"this\"",
    "\"throw\"",
    "\"throws\"",
    "\"transient\"",
    "\"true\"",
    "\"try\"",
    "\"void\"",
    "\"volatile\"",
    "\"while\"",
    "<INTEGER_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<CHARACTER_LITERAL>",
    "<STRING_LITERAL>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\">\"",
    "\"<\"",
    "\"!\"",
    "\"~\"",
    "\"?\"",
    "\":\"",
    "\"==\"",
    "\"<=\"",
    "\">=\"",
    "\"!=\"",
    "\"||\"",
    "\"&&\"",
    "\"++\"",
    "\"--\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"&\"",
    "\"|\"",
    "\"^\"",
    "\"%\"",
    "\"<<\"",
    "\">>\"",
    "\">>>\"",
    "\"+=\"",
    "\"-=\"",
    "\"*=\"",
    "\"/=\"",
    "\"&=\"",
    "\"|=\"",
    "\"^=\"",
    "\"%=\"",
    "\"<<=\"",
    "\">>=\"",
    "\">>>=\"",
  };

}
