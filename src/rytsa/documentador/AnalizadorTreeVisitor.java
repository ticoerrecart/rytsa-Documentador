package rytsa.documentador;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import test.helper.ClassInfoDataSetter;
import test.helper.FieldInfoDataSetter;
import test.helper.MethodInfoDataSetter;
import test.model.JavaClassInfo;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree.JCIdent;

/**
 * Visitor class which visits different nodes of the input source file, 
 * extracts the required atribute of the visiting class, its mehods, 
 * fields, annotations etc and set it in the java class model.
 * 
 * @author Seema Richard (Seema.Richard@ust-global.com)
 * @author Deepa Sobhana (Deepa.Sobhana@ust-global.com)
 */
public class AnalizadorTreeVisitor extends TreePathScanner<Object, Trees> {

    
    
    ClaseBean bean = null;

    /**
     * Visits the class
     * @param classTree
     * @param trees
     * @return
     */
    @Override
    public Object visitClass(ClassTree classTree, Trees trees) {

        TreePath path = getCurrentPath();
             
        TypeElement e = (TypeElement) trees.getElement(path);
        if (e!=null){
        	bean = new ClaseBean();
        	bean.setNombre(e.getSimpleName().toString());
            bean.setPaquete(e.getEnclosingElement().toString());
            bean.setTipo(e.getKind().toString());
            bean.setSubtipo(e.getModifiers().toString());
            bean.setDescripcion("?"); //TODO -> podria ser main si tiene un metodo main	
        }
        
        return super.visitClass(classTree, trees);
    }

    /**
     * Visits all methods of the input java source file
     * @param methodTree
     * @param trees
     * @return
     */
    @Override
    public Object visitMethod(MethodTree methodTree, Trees trees) {
        /*TreePath path = getCurrentPath();
        populate required method information to model
        MethodInfoDataSetter.populateMethodInfo(clazzInfo, methodTree, 
                                                path, trees);*/
        return super.visitMethod(methodTree, trees);
    }

    /**
     * Visits all variables of the java source file
     * @param variableTree
     * @param trees
     * @return
     */
    @Override
    public Object visitVariable(VariableTree variableTree, Trees trees) {
       /* TreePath path = getCurrentPath();
        Element e = trees.getElement(path);
	
        //populate required method information to model
        FieldInfoDataSetter.populateFieldInfo(clazzInfo, variableTree, e, 
                                              path, trees);*/
    	try {    	
	    	ClaseBean cb = new ClaseBean();
	    	bean.getVariables().add(cb);
	    	
	    	String fullName = ((JCIdent)variableTree.getType()).sym.toString();
	    	int lastPoint = fullName.lastIndexOf('.');
	    	
	    	cb.setNombre(fullName.substring(lastPoint+1));
	    	cb.setNombreInstancia(variableTree.getName().toString());
	    	cb.setPaquete(fullName.substring(0,lastPoint));
	    	//cb.setTipo(variableTree.getModifiers().toString());
	    	cb.setSubtipo(variableTree.getModifiers().toString());
	    	if (variableTree.getInitializer() != null) {
				cb.setCardinalidad("Instancia");
			} else{
				cb.setCardinalidad("Parameter");
			}
    	} catch (Exception e) {
    		System.out.println("ERROR -> " +  e.toString());
    	}	
        return super.visitVariable(variableTree, trees);
    }
}

