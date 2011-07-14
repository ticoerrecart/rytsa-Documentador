package rytsa.documentador;

import java.util.List;

import javax.lang.model.element.TypeElement;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * Visitor class which visits different nodes of the input source file, extracts
 * the required atribute of the visiting class, its mehods, fields, annotations
 * etc and set it in the java class model.
 * 
 * @author Seema Richard (Seema.Richard@ust-global.com)
 * @author Deepa Sobhana (Deepa.Sobhana@ust-global.com)
 */
public class AnalizadorTreeVisitor extends TreePathScanner<Object, Trees> {

	ClaseBean bean = null;

	/**
	 * Visits the class
	 * 
	 * @param classTree
	 * @param trees
	 * @return
	 */
	@Override
	public Object visitClass(ClassTree classTree, Trees trees) {

		TreePath path = getCurrentPath();

		TypeElement e = (TypeElement) trees.getElement(path);
		if (e != null) {
			bean = new ClaseBean();
			bean.setNombre(e.getSimpleName().toString());
			bean.setPaquete(e.getEnclosingElement().toString());
			//bean.setTipo(e.getKind().toString());
			//bean.setSubtipo("java");
		}

		return super.visitClass(classTree, trees);

	}

	/**
	 * Visits the import
	 * 
	 * @param importTree
	 * @param trees
	 * @return
	 */
	@Override
	public Object visitImport(ImportTree importTree, Trees trees) {

		ClaseBean cb = new ClaseBean();
		System.out.println(" clases " + importTree.getClass());

		bean.getReferencias().add(cb);
		cargarDatosImport(importTree, cb);
		return super.visitImport(importTree, trees);

	}

	@Override
	public Object visitMethodInvocation(MethodInvocationTree mi, Trees trees) {

		ClaseBean cb = new ClaseBean();

		// System.out.println(" mi.getClass().getPackage(); " +
		// mi.getMethodSelect().getKind());
		// System.out.println(" mi.getClass() " + mi.getClass());
		// System.out.println(" mi.getKind " + mi.getKind());
		// System.out.println(" mi.getMethodSelect " +
		// mi.getMethodSelect().toString());

		String claseStatic = mi.getMethodSelect().toString();

		int point = claseStatic.indexOf('.');
		if (point > -1) {

			cb.setNombre(claseStatic.substring(0, point));
			cb.setCardinalidad("POU");
			//cb.setTipo("CLASS");
			//cb.setSubtipo("java");
			registroEstaticaSiNoExiste(bean.getClasesEstaticas(), cb);
		}

		return super.visitMethodInvocation(mi, trees);
	}

	/**
	 * Visits all methods of the input java source file
	 * 
	 * @param methodTree
	 * @param trees
	 * @return
	 */
	@Override
	public Object visitMethod(MethodTree methodTree, Trees trees) {

		ClaseBean cb = new ClaseBean();
		// System.out.println(" clases " + methodTree.getName());

		bean.getMetodos().add(cb);
		bean.setDescripcion("");

		// Si esta clase tiene un método main, completo la descripción con ese
		// dato
		if (methodTree.getName().contentEquals("main")) {
			bean.setDescripcion("main");
		}

		cb.setNombre(methodTree.getClass().getSimpleName().toString());
		cb.setPaquete(methodTree.getClass().getEnclosingClass().toString());
		//cb.setTipo(methodTree.getKind().toString());
		//cb.setSubtipo("java");

		return super.visitMethod(methodTree, trees);
	}

	/**
	 * Visits all variables of the java source file
	 * 
	 * @param variableTree
	 * @param trees
	 * @return
	 */
	@Override
	public Object visitVariable(VariableTree variableTree, Trees trees) {
		/*
		 * TreePath path = getCurrentPath(); Element e = trees.getElement(path);
		 * 
		 * //populate required method information to model
		 * FieldInfoDataSetter.populateFieldInfo(clazzInfo, variableTree, e,
		 * path, trees);
		 */
		try {
			ClaseBean cb = new ClaseBean();
			

			cargarDatosVariable(variableTree, cb);

			cb.setNombreInstancia(variableTree.getName().toString());

			//cb.setTipo("CLASS");
			//cb.setSubtipo("java");
			
			if (variableTree.getInitializer() != null) {
				cb.setCardinalidad("Instancia");
			} else {
				cb.setCardinalidad("UPD");
			}
		
			registroVariableSiNoExiste(bean.getVariables(), cb);
		
		} catch (Exception e) {
			System.out.println("ERROR -> " + e.toString());
		}
				
		return super.visitVariable(variableTree, trees);
	}

	void cargarDatosVariable(VariableTree variableTree, ClaseBean cb) {

		if (variableTree.getType() instanceof JCVariableDecl) {
			cargarDatosVariable((JCVariableDecl) variableTree.getType(), cb);
		} else if (variableTree.getType() instanceof JCPrimitiveTypeTree) {
			cargarDatosVariable((JCPrimitiveTypeTree) variableTree.getType(),
					cb);
		} else if (variableTree.getType() instanceof JCIdent) {
			cargarDatosVariable((JCIdent) variableTree.getType(), cb);
		} else if (variableTree.getType() instanceof JCAnnotation) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCAssign) {

			System.out.println(" ver qué hacer  -> " + variableTree.getType());
		} else if (variableTree.getType() instanceof JCBlock) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());
		} else if (variableTree.getType() instanceof JCCompilationUnit) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCExpression) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCFieldAccess) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCLabeledStatement) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCLiteral) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCMethodDecl) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCMethodInvocation) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCStatement) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());

		} else if (variableTree.getType() instanceof JCTree) {
			System.out.println(" ver qué hacer  -> " + variableTree.getType());
		} else {
			System.out.println("ERROR -> " + variableTree.toString());

		}

	}

	void cargarDatosVariable(JCVariableDecl variableTree, ClaseBean cb) {
		cb.setNombre(variableTree.getType().toString());
		// System.out.println("ERROR SYM NULL Dentro del vartype -> " +
		// variableTree.toString());
	}

	void cargarDatosVariable(JCPrimitiveTypeTree variableTree, ClaseBean cb) {
		cb.setNombre(variableTree.toString());
		cb.setPaquete("<<Primitivo>>");
	}

	void cargarDatosVariable(JCIdent variableTree, ClaseBean cb) {
		if (variableTree.sym != null) {
			String fullName = variableTree.sym.toString();
			int lastPoint = fullName.lastIndexOf('.');
			cb.setNombre(fullName.substring(lastPoint + 1));
			cb.setPaquete(fullName.substring(0, lastPoint));
		} else {
			cb.setNombre(variableTree.toString());
			// System.out.println("ERROR SYM NULL -> " +
			// variableTree.toString());
		
		}
	}

	void cargarDatosImport(ImportTree importTree, ClaseBean cb) {
		System.out.println("importTree.toString() " + importTree.toString()
				+ "importTree.getClass().getSimpleName().toString() "
				+ importTree.getClass().getSimpleName().toString()
				+ "importTree.getClass().getEnclosingClass().toString() "
				+ importTree.getClass().getEnclosingClass().toString()
				+ "importTree.getKind().toString() "
				+ importTree.getKind().toString());

		cb.setNombre(importTree.getClass().getSimpleName().toString());
		cb.setPaquete(importTree.getClass().getEnclosingClass().toString());
		//cb.setTipo(importTree.getKind().toString());
		//cb.setSubtipo("java");

	}

	private void registroEstaticaSiNoExiste(List<ClaseBean> clasesEstaticas,
			ClaseBean cb) {
		boolean llamadoAClaseEstaticaExiste = false;
		for (ClaseBean b : clasesEstaticas) {
			if (b.getNombre().equals(cb.getNombre()))
				llamadoAClaseEstaticaExiste = true;
		}
		if (!llamadoAClaseEstaticaExiste)
			bean.getClasesEstaticas().add(cb);
	}

	private void registroVariableSiNoExiste(List<ClaseBean> variables,
			ClaseBean cb) {
		boolean existe = false;
		for (ClaseBean b : variables) {
			if (b.getNombre()!= null) {
				if (b.getNombre().equals(cb.getNombre()))
					existe = true;
			}
		}
		if (!existe)
			bean.getVariables().add(cb);
	}

}
