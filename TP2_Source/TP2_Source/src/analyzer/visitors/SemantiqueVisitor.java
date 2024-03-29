package analyzer.visitors;

import analyzer.SemantiqueError;
import analyzer.ast.*;

import javax.xml.crypto.Data;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created: 19-01-10
 * Last Changed: 19-01-25
 * Author: Félix Brunet
 * <p>
 * Description: Ce visiteur explorer l'AST est renvois des erreur lorqu'une erreur sémantique est détecté.
 */

public class SemantiqueVisitor implements ParserVisitor {

    private final PrintWriter m_writer;

    private HashMap<String, VarType> SymbolTable = new HashMap<>(); // mapping variable -> type

    // variable pour les metrics
    private int VAR = 0;
    private int WHILE = 0;
    private int IF = 0;
    private int FOR = 0;
    private int OP = 0;
    private boolean error = false;

    public SemantiqueVisitor(PrintWriter writer) {
        m_writer = writer;
    }

    /*
    Le Visiteur doit lancer des erreurs lorsqu'un situation arrive.

    regardez l'énoncé ou les tests pour voir le message à afficher et dans quelle situation.
    Lorsque vous voulez afficher une erreur, utilisez la méthode print implémentée ci-dessous.
    Tous vos tests doivent passer!!

     */

    @Override
    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, data);
        print(String.format("{VAR:%d, WHILE:%d, IF:%d, FOR:%d, OP:%d}", VAR, WHILE, IF, FOR, OP));
        return null;
    }

    /*
    Appelez cette méthode pour afficher vos erreurs.
     */
    private void print(final String msg) {
        if (!error) {
            m_writer.print(msg);
            error = true;
        }
    }

    /*
    Ici se retrouve les noeuds servant à déclarer une variable.
    Certaines doivent enregistrer les variables avec leur type dans la table symbolique.
     */
    @Override
    public Object visit(ASTDeclaration node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTNormalDeclaration node, Object data) {
        String varName = ((ASTIdentifier) node.jjtGetChild(0)).getValue();

        if (SymbolTable.containsKey(varName))
            print("Invalid declaration... variable " + varName + " already exists");
        else {
            SymbolTable.put(varName, node.getValue().equals("num") ? VarType.num : VarType.bool);
            VAR++;
        }
        return null;
    }

    @Override
    public Object visit(ASTListDeclaration node, Object data) {
        String varName = ((ASTIdentifier) node.jjtGetChild(0)).getValue();

        if (SymbolTable.containsKey(varName))
            print("Invalid declaration... variable " + varName + " already exists");
        else {
            SymbolTable.put(varName, node.getValue().equals("listnum") ? VarType.listnum : VarType.listbool);
            VAR++;
        }
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }


    @Override
    public Object visit(ASTStmt node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    /*
     * Il faut vérifier que le type déclaré à gauche soit compatible avec la liste utilisée à droite. N'oubliez pas
     * de vérifier que les variables existent.
     */

    @Override
    public Object visit(ASTForEachStmt node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);

        String declaredName = ((ASTIdentifier)node.jjtGetChild(0).jjtGetChild(0)).getValue();
        String arrayName = ((ASTIdentifier)node.jjtGetChild(1)).getValue();

        VarType arrayType = SymbolTable.get(arrayName);
        boolean isBoolValid = SymbolTable.get(declaredName) == VarType.bool && arrayType == VarType.listbool;
        boolean isNumValid = SymbolTable.get(declaredName) == VarType.num && arrayType == VarType.listnum;

        if(arrayType != VarType.listnum && arrayType != VarType.listbool)
            print("Array type is required here...");
        else if(!isBoolValid && !isNumValid) {
            print("Array type "+ arrayType + " is incompatible with declared variable of type "
                    + SymbolTable.get(declaredName) + "...");
        }

        node.jjtGetChild(2).jjtAccept(this, data);
        FOR++;
        return null;
    }

    private void callChildrenCond(SimpleNode node) {
        DataStruct ds = new DataStruct();
        node.jjtGetChild(0).jjtAccept(this, ds);

        if (ds.type != VarType.bool)
            throw new SemantiqueError("Invalid type in condition.");

        for (int i = 1; i < node.jjtGetNumChildren(); i++)
            node.jjtGetChild(i).jjtAccept(this, ds);
    }

    /*
    les structures conditionnelle doivent vérifier que leur expression de condition est de type booléenne
    On doit aussi compter les conditions dans les variables IF et WHILE
     */
    @Override
    public Object visit(ASTIfStmt node, Object data) {
        callChildrenCond(node);
        IF++;
        return null;
    }

    @Override
    public Object visit(ASTWhileStmt node, Object data) {
        callChildrenCond(node);
        WHILE++;
        return null;
    }

    /*
    On doit vérifier que le type de la variable est compatible avec celui de l'expression.
    La variable doit etre déclarée.
     */
    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        String varName = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        DataStruct ds = new DataStruct();
        node.childrenAccept(this, ds);

        if(SymbolTable.get(varName) != ds.type) {
            print("Invalid type in assignation of Identifier " + varName + "... was expecting " + SymbolTable.get(varName)
                    + " but got " + ds.type);
        }
        return null;
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        //Il est normal que tous les noeuds jusqu'à expr retourne un type.
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTCompExpr node, Object data) {
        /*attention, ce noeud est plus complexe que les autres.
        si il n'a qu'un seul enfant, le noeud a pour type le type de son enfant.

        si il a plus d'un enfant, alors ils s'agit d'une comparaison. il a donc pour type "Bool".

        de plus, il n'est pas acceptable de faire des comparaisons de booleen avec les opérateur < > <= >=.
        les opérateurs == et != peuvent être utilisé pour les nombres et les booléens, mais il faut que le type soit le même
        des deux côté de l'égalité/l'inégalité.
        */

        if(node.jjtGetNumChildren() > 1) {
            node.jjtGetChild(0).jjtAccept(this, data);
            DataStruct ds = new DataStruct();
            node.jjtGetChild(1).jjtAccept(this, ds);

            if(ds.type != ((DataStruct)data).type)
                print("Invalid type in expression");
            if(((DataStruct)data).type == VarType.bool && ds.type == VarType.bool) {
                if(!(node.getValue().equals("!=") || node.getValue().equals("==")))
                    print("Invalid type in expression");
            }
            ((DataStruct) data).type = VarType.bool;
            OP += node.jjtGetNumChildren() - 1;
        } else
            node.childrenAccept(this, data);
        return null;
    }

    private void callChildren(SimpleNode node, Object data, VarType validType) {
        if (node.jjtGetNumChildren() > 1) {
            DataStruct ds = new DataStruct();
            for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
                node.jjtGetChild(i).jjtAccept(this, ds);
                if (ds.type != validType) {
                    print("Invalid type in expression");
                    break;
                }
            }
            ((DataStruct) data).type = validType;
            OP += node.jjtGetNumChildren() - 1;
        } else
            node.childrenAccept(this, data);
    }

    /*
    opérateur binaire
    si il n'y a qu'un enfant, aucune vérification à faire.
    par exemple, un AddExpr peut retourner le type "Bool" à condition de n'avoir qu'un seul enfant.
     */
    @Override
    public Object visit(ASTAddExpr node, Object data) {
        callChildren(node, data, VarType.num);
        return null;
    }

    @Override
    public Object visit(ASTMulExpr node, Object data) {
        callChildren(node, data, VarType.num);
        return null;
    }

    @Override
    public Object visit(ASTBoolExpr node, Object data) {
        callChildren(node, data, VarType.bool);
        return null;
    }

    /*
    opérateur unaire
    les opérateur unaire ont toujours un seul enfant.

    Cependant, ASTNotExpr et ASTUnaExpr ont la fonction "getOps()" qui retourne un vecteur contenant l'image (représentation str)
    de chaque token associé au noeud.

    Il est utile de vérifier la longueur de ce vecteur pour savoir si une opérande est présente.

    si il n'y a pas d'opérande, ne rien faire.
    si il y a une (ou plus) opérande, ils faut vérifier le type.

    */
    @Override
    public Object visit(ASTNotExpr node, Object data) {

        if (!node.getOps().isEmpty()) {
            DataStruct ds = new DataStruct();
            node.jjtGetChild(0).jjtAccept(this, ds);
            if (ds.type != VarType.bool)
                print("Invalid type in expression");
            ((DataStruct) data).type = VarType.bool;
            OP += node.getOps().size();
        } else
            node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTUnaExpr node, Object data) {

        if (!node.getOps().isEmpty()) {
            DataStruct ds = new DataStruct();
            node.jjtGetChild(0).jjtAccept(this, ds);
            if (ds.type != VarType.num)
                print("Invalid type in expression");
            ((DataStruct) data).type = VarType.num;
            OP += node.getOps().size();
        } else
            node.childrenAccept(this, data);
        return null;
    }

    /*

    les noeudS ASTIdentifier aillant comme parent "GenValue" doivent vérifier leur type et vérifier leur existence.

    Ont peut envoyer une information a un enfant avec le 2e paramètre de jjtAccept ou childrenAccept.

     */
    @Override
    public Object visit(ASTGenValue node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }


    @Override
    public Object visit(ASTBoolValue node, Object data) {
        ((DataStruct) data).type = VarType.bool;
        return null;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        if(!SymbolTable.containsKey(node.getValue()))
            print("Invalid use of undefined Identifier " + node.getValue());
        else if (node.jjtGetParent() instanceof ASTGenValue) {
            VarType varType = SymbolTable.get(node.getValue());
            ((DataStruct) data).type = varType;
        }
        return null;
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        ((DataStruct) data).type = VarType.num;
        return null;
    }


    //des outils pour vous simplifier la vie et vous enligner dans le travail
    public enum VarType {
        bool,
        num,
        listnum,
        listbool,
    }

    private class DataStruct {
        public VarType type;

        public DataStruct() {
        }

        public DataStruct(VarType p_type) {
            type = p_type;
        }
    }
}
