package rytsa.documentador;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.annotation.processing.AbstractProcessor;
import javax.swing.filechooser.FileFilter;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import test.CodeAnalyzerProcessor;

public class Documentador {
	public File fileOrDirectory;
	public String proyecto;
	public String separador;
	
	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public File getFileOrDirectory() {
		return fileOrDirectory;
	}

	public void setFileOrDirectory(File fileOrDirectory) {
		this.fileOrDirectory = fileOrDirectory;
	}
	
	public void documentar(){
		//recorro todo los archivos dado un zip un file o un directorio
		if (this.fileOrDirectory.isDirectory()){

		}
		//supongamos por ahora un .java
		//fileOrDirectory
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		
		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjects(fileOrDirectory); 

		CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits1);
		// Get the list of annotation processors
		LinkedList<AbstractProcessor> processors = new LinkedList<AbstractProcessor>();
		processors.add(new Analizador());
		task.setProcessors(processors);
		// 	Perform the compilation task.
		task.call();
		try {
			fileManager.close();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
