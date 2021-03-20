package analyzer.visitors;

import analyzer.ast.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Created: 19-02-15
 * Last Changed: 20-10-6
 * Author: Félix Brunet & Doriane Olewicki
 *
 * Description: Ce visiteur explore l'AST et génère un code intermédiaire.
 */

public class IntermediateCodeGenVisitor implements ParserVisitor {

    //le m_writer est un Output_Stream connecter au fichier "result". c'est donc ce qui permet de print dans les fichiers
    //le code généré.
    private final PrintWriter m_writer;

    public IntermediateCodeGenVisitor(PrintWriter writer) {
        m_writer = writer;
    }
    public HashMap<String, VarType> SymbolTable = new HashMap<>();

    private int id = 0;
    private int label = 0;
    /*
    génère une nouvelle variable temporaire qu'il est possible de print
    À noté qu'il serait possible de rentrer en conflit avec un nom de variable définit dans le programme.
    Par simplicité, dans ce tp, nous ne concidérerons pas cette possibilité, mais il faudrait un générateur de nom de
    variable beaucoup plus robuste dans un vrai compilateur.
     */
    private String genId() {
        return "_t" + id++;
    }

    //génère un nouveau Label qu'il est possible de print.
    private String genLabel() {
        return "_L" + label++;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTProgram node, Object data)  {
        String label = genLabel();
        node.childrenAccept(this, label);
        m_writer.println(label);
        return null;
    }

    /*
    Code fournis pour remplir la table de symbole.
    Les déclarations ne sont plus utile dans le code à trois adresse.
    elle ne sont donc pas concervé.
     */
    @Override
    public Object visit(ASTDeclaration node, Object data) {
        ASTIdentifier id = (ASTIdentifier) node.jjtGetChild(0);
        VarType t;
        if (node.getValue().equals("bool"))
            t = VarType.Bool;
        else
            t = VarType.Number;

        SymbolTable.put(id.getValue(), t);
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        String s2 = (String) data;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i != node.jjtGetNumChildren() -1) {
                String s1 = genLabel();
                node.jjtGetChild(i).jjtAccept(this, s1);
                m_writer.println(s1);
            } else
                node.jjtGetChild(i).jjtAccept(this, s2);
        }
        return null;
    }

    @Override
    public Object visit(ASTStmt node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    /*
    le If Stmt doit vérifier s'il à trois enfants pour savoir s'il s'agit d'un "if-then" ou d'un "if-then-else".
     */
    @Override
    public Object visit(ASTIfStmt node, Object data) {
        if (node.jjtGetNumChildren() == 2) {
            BoolLabel b = new BoolLabel(genLabel(), (String) data);
            node.jjtGetChild(0).jjtAccept(this, b);
            m_writer.println(b.lTrue);
            node.jjtGetChild(1).jjtAccept(this, data);
        } else {
            BoolLabel b = new BoolLabel(genLabel(), genLabel());
            node.jjtGetChild(0).jjtAccept(this, b);
            m_writer.println(b.lTrue);
            node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println("goto " + data);
            m_writer.println(b.lFalse);
            node.jjtGetChild(2).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTWhileStmt node, Object data) {
        String begin = genLabel();
        BoolLabel b = new BoolLabel(genLabel(), (String) data);
        m_writer.println(begin);
        node.jjtGetChild(0).jjtAccept(this, b);
        m_writer.println(b.lTrue);
        node.jjtGetChild(1).jjtAccept(this, begin);
        m_writer.println("goto " + begin);
        return null;
    }


    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        String id = (String)node.jjtGetChild(0).jjtAccept(this, null);;
        if (SymbolTable.get(id) == VarType.Number) {
            String expr = (String) node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println(id + " = " + expr);
        } else {
            BoolLabel b = new BoolLabel(genLabel(), genLabel());
            node.jjtGetChild(1).jjtAccept(this, b);
            m_writer.println(b.lTrue);
            m_writer.println(id + " = " + 1);
            m_writer.println("goto " + data);
            m_writer.println(b.lFalse);
            m_writer.println(id + " = " + 0);
        }
        return null;
    }



    @Override
    public Object visit(ASTExpr node, Object data){
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    //Expression arithmétique
    /*
    Les expressions arithmétique add et mult fonctionne exactement de la même manière. c'est pourquoi
    il est plus simple de remplir cette fonction une fois pour avoir le résultat pour les deux noeuds.

    On peut bouclé sur "ops" ou sur node.jjtGetNumChildren(),
    la taille de ops sera toujours 1 de moins que la taille de jjtGetNumChildren
     */
    public Object codeExtAddMul(SimpleNode node, Object data, Vector<String> ops) {
        if (node.jjtGetNumChildren() == 2) {
            String temp = genId();
            String e1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String e2 = (String) node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println(temp + " = " + e1 + " " + ops.get(0) + " " + e2);
            return temp;
        } else
            return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTAddExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTMulExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    //UnaExpr est presque pareil au deux précédente. la plus grosse différence est qu'il ne va pas
    //chercher un deuxième noeud enfant pour avoir une valeur puisqu'il s'agit d'une opération unaire.
    @Override
    public Object visit(ASTUnaExpr node, Object data) {
        if (!node.getOps().isEmpty()) {
            String expr = (String) node.jjtGetChild(0).jjtAccept(this, data);
            String temp = null;
            for (int i = 0; i < node.getOps().size(); ++i) {
                temp = genId();
                m_writer.println(temp + " = " + node.getOps().get(i) + " " + expr);
                expr = temp;
            }
            return temp;
        } else
            return node.jjtGetChild(0).jjtAccept(this, data);
    }

    //expression logique
    @Override
    public Object visit(ASTBoolExpr node, Object data) {
        if (!node.getOps().isEmpty()) {
            BoolLabel b = (BoolLabel) data;
            BoolLabel b1 = new BoolLabel("", "");
            BoolLabel b2 = new BoolLabel("", "");
            if (node.getOps().get(0).equals("&&")) {
                b1.lTrue = genLabel();
                b1.lFalse = b.lFalse;
                b2.lTrue = b.lTrue;
                b2.lFalse = b.lFalse;
                node.jjtGetChild(0).jjtAccept(this, b1);
                m_writer.println(b1.lTrue);
                node.jjtGetChild(1).jjtAccept(this, b2);
            }
            else if (node.getOps().get(0).equals("||")) {
                b1.lTrue = b.lTrue;
                b1.lFalse = genLabel();
                b2.lTrue = b.lTrue;
                b2.lFalse = b.lFalse;
                node.jjtGetChild(0).jjtAccept(this, b1);
                m_writer.println(b1.lFalse);
                node.jjtGetChild(1).jjtAccept(this, b2);
            }
            return null;
        } else
            return node.jjtGetChild(0).jjtAccept(this, data);
    }


    @Override
    public Object visit(ASTCompExpr node, Object data) {
        if (node.jjtGetNumChildren() > 1) {
            BoolLabel b = (BoolLabel) data;
            String e1 = (String) node.jjtGetChild(0).jjtAccept(this, null);
            String e2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
            m_writer.println("if " +  e1 + " " + node.getValue() + " " + e2 + " goto " + b.lTrue);
            m_writer.println("goto " + b.lFalse);
            return null;
        } else
            return node.jjtGetChild(0).jjtAccept(this, data);
    }


    /*
    Même si on peut y avoir un grand nombre d'opération, celle-ci s'annullent entre elle.
    il est donc intéressant de vérifier si le nombre d'opération est pair ou impaire.
    Si le nombre d'opération est pair, on peut simplement ignorer ce noeud.
     */
    @Override
    public Object visit(ASTNotExpr node, Object data) {
        if (!node.getOps().isEmpty() && (node.getOps().size() % 2 != 0)) {
            BoolLabel b = (BoolLabel) data;
            BoolLabel b1 = new BoolLabel(b.lFalse, b.lTrue);
            return node.jjtGetChild(0).jjtAccept(this, b1);
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTGenValue node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    /*
    BoolValue ne peut pas simplement retourné sa valeur à son parent contrairement à GenValue et IntValue,
    Il doit plutôt généré des Goto direct, selon sa valeur.
     */
    @Override
    public Object visit(ASTBoolValue node, Object data) {
        if (node.getValue())
            m_writer.println("goto " + ((BoolLabel) data).lTrue);
        else
            m_writer.println("goto " + ((BoolLabel) data).lFalse);
        return node.getValue();
    }


    /*
    si le type de la variable est booléenne, il faudra généré des goto ici.
    le truc est de faire un "if value == 1 goto Label".
    en effet, la structure "if valeurBool goto Label" n'existe pas dans la syntaxe du code à trois adresse.
     */
    @Override
    public Object visit(ASTIdentifier node, Object data) {
        if (SymbolTable.get(node.getValue()) == VarType.Bool && data != null) {
            BoolLabel b = (BoolLabel) data;
            m_writer.println("if " + node.getValue() + " == 1 goto " + b.lTrue );
            m_writer.println("goto " + b.lFalse);
        }
        return node.getValue();
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        return Integer.toString(node.getValue());
    }


    @Override
    public Object visit(ASTSwitchStmt node, Object data) {
        String cases = genLabel();
        m_writer.println("goto " + cases);
        String whileValue = (String) node.jjtGetChild(0).jjtAccept(this, data);
        HashMap<String, String> caseValues = new HashMap<>();

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            String caseLabel = genLabel();
            m_writer.println(caseLabel);
            String caseValue = (String) node.jjtGetChild(i).jjtAccept(this, data);
            caseValues.put(caseValue, caseLabel);
        }
        m_writer.println(cases);

        for (Map.Entry<String, String> caseValue : caseValues.entrySet()) {
            if (!caseValue.getKey().equals("default"))
                m_writer.println("if " + whileValue + " == " + caseValue.getKey() + " goto " + caseValue.getValue());
        }

        if (caseValues.containsKey("default"))
            m_writer.println("goto " + caseValues.get("default"));

        return null;
    }

    @Override
    public Object visit(ASTCaseStmt node, Object data) {
        String value = (String) node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);
        m_writer.println("goto " + data);
        return value;
    }

    @Override
    public Object visit(ASTDefaultStmt node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        m_writer.println("goto " + data);
        return "default";
    }

    //des outils pour vous simplifier la vie et vous enligner dans le travail
    public enum VarType {
        Bool,
        Number
    }

    //utile surtout pour envoyé de l'informations au enfant des expressions logiques.
    private class BoolLabel {
        public String lTrue = null;
        public String lFalse = null;

        public BoolLabel(String t, String f) {
            lTrue = t;
            lFalse = f;
        }
    }


}
