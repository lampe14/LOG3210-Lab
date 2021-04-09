/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package analyzer.ast;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 5;
  /** RegularExpression Id. */
  int FORMAL_COMMENT = 6;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 7;
  /** RegularExpression Id. */
  int IF = 9;
  /** RegularExpression Id. */
  int THEN = 10;
  /** RegularExpression Id. */
  int ELSE = 11;
  /** RegularExpression Id. */
  int WHILE = 12;
  /** RegularExpression Id. */
  int SWITCH = 13;
  /** RegularExpression Id. */
  int CASE = 14;
  /** RegularExpression Id. */
  int DEF = 15;
  /** RegularExpression Id. */
  int BOOLEAN = 16;
  /** RegularExpression Id. */
  int TRUE = 17;
  /** RegularExpression Id. */
  int FALSE = 18;
  /** RegularExpression Id. */
  int MATCH = 19;
  /** RegularExpression Id. */
  int WITH = 20;
  /** RegularExpression Id. */
  int DO = 21;
  /** RegularExpression Id. */
  int INPUT = 22;
  /** RegularExpression Id. */
  int OUTPUT = 23;
  /** RegularExpression Id. */
  int DEC = 24;
  /** RegularExpression Id. */
  int ENDDEC = 25;
  /** RegularExpression Id. */
  int TYPE = 26;
  /** RegularExpression Id. */
  int BOOL = 27;
  /** RegularExpression Id. */
  int NUM = 28;
  /** RegularExpression Id. */
  int ASSIGN = 29;
  /** RegularExpression Id. */
  int COMPARE = 30;
  /** RegularExpression Id. */
  int MULOP = 31;
  /** RegularExpression Id. */
  int BOOLOP = 32;
  /** RegularExpression Id. */
  int PLUS = 33;
  /** RegularExpression Id. */
  int MINUS = 34;
  /** RegularExpression Id. */
  int EQUAL = 35;
  /** RegularExpression Id. */
  int LESS = 36;
  /** RegularExpression Id. */
  int LESSEQUAL = 37;
  /** RegularExpression Id. */
  int GREAT = 38;
  /** RegularExpression Id. */
  int GREATEQUAL = 39;
  /** RegularExpression Id. */
  int DIFF = 40;
  /** RegularExpression Id. */
  int EQUALEQUAL = 41;
  /** RegularExpression Id. */
  int FOIS = 42;
  /** RegularExpression Id. */
  int DIV = 43;
  /** RegularExpression Id. */
  int MOD = 44;
  /** RegularExpression Id. */
  int POW = 45;
  /** RegularExpression Id. */
  int AND = 46;
  /** RegularExpression Id. */
  int OR = 47;
  /** RegularExpression Id. */
  int NOT = 48;
  /** RegularExpression Id. */
  int LPAREN = 49;
  /** RegularExpression Id. */
  int RPAREN = 50;
  /** RegularExpression Id. */
  int LACC = 51;
  /** RegularExpression Id. */
  int RACC = 52;
  /** RegularExpression Id. */
  int COLON = 53;
  /** RegularExpression Id. */
  int ENDSTMT = 54;
  /** RegularExpression Id. */
  int COMMA = 55;
  /** RegularExpression Id. */
  int IDENTIFIER = 56;
  /** RegularExpression Id. */
  int LETTER = 57;
  /** RegularExpression Id. */
  int DIGIT = 58;
  /** RegularExpression Id. */
  int INTEGER = 59;
  /** RegularExpression Id. */
  int REAL = 60;
  /** RegularExpression Id. */
  int EXPONENT = 61;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int IN_SINGLE_LINE_COMMENT = 1;
  /** Lexical state. */
  int IN_FORMAL_COMMENT = 2;
  /** Lexical state. */
  int IN_MULTI_LINE_COMMENT = 3;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<token of kind 1>",
    "\"//\"",
    "<token of kind 3>",
    "\"/*\"",
    "<SINGLE_LINE_COMMENT>",
    "\"*/\"",
    "\"*/\"",
    "<token of kind 8>",
    "\"if\"",
    "\"then\"",
    "\"else\"",
    "\"while\"",
    "\"switch\"",
    "\"case\"",
    "\"default\"",
    "<BOOLEAN>",
    "\"true\"",
    "\"false\"",
    "\"match\"",
    "\"with\"",
    "\"do\"",
    "\"INPUT\"",
    "\"OUTPUT\"",
    "\"Declaration\"",
    "\"EndDeclaration\"",
    "<TYPE>",
    "\"bool\"",
    "\"num\"",
    "<ASSIGN>",
    "<COMPARE>",
    "<MULOP>",
    "<BOOLOP>",
    "\"+\"",
    "\"-\"",
    "\"=\"",
    "\"<\"",
    "\"<=\"",
    "\">\"",
    "\">=\"",
    "\"!=\"",
    "\"==\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "\"**\"",
    "\"&&\"",
    "\"||\"",
    "\"!\"",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\":\"",
    "\";\"",
    "\",\"",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "<INTEGER>",
    "<REAL>",
    "<EXPONENT>",
  };

}
