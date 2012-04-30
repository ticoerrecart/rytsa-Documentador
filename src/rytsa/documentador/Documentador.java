package rytsa.documentador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Documentador {
	public File fileOrDirectory;
	public String proyecto;
	public String separador;
	private Analizador analizador;// = new Analizador();
	private String tabla1;
	private String tabla5;
	
	public String getTabla1() {
		return tabla1;
	}

	public void setTabla1(String tabla1) {
		this.tabla1 = tabla1;
	}

	public Documentador() {
		Calendar cal = Calendar.getInstance();
		
		tabla1 =  "OJ-" + Integer.toString(cal.get(Calendar.DATE)) + "-" + Integer.toString(cal.get(Calendar.MONTH)+1) + "-" + Integer.toString(cal.get(Calendar.YEAR)) + ".txt";
		tabla5 =  "CR-" + Integer.toString(cal.get(Calendar.DATE)) + "-" + Integer.toString(cal.get(Calendar.MONTH)+1) + "-" + Integer.toString(cal.get(Calendar.YEAR)) + ".txt";
	
	}

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

	private void descomprimirZip(File file, List<File> filesSalida)
			throws IOException {
		// FileUtil.getInstance().decompressZipFile(file.getAbsolutePath(),
		// "c:\\documentador\\");
		Enumeration entries;
		ZipFile zipFile = null;
		File rootDir = null;
		zipFile = new ZipFile(file);
		// processing zip file entries
		entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			if (!entry.isDirectory()) {
				if (seDebeAgregarFile(file)) {
					filesSalida.add(file);
				}
			}
		}
	}

	private boolean seDebeAgregarFile(File file) {
		return file.getName().endsWith(".java")  ;
	}

	private void recuperarFiles(File file, List<File> filesSalida)
			throws IOException {
		if (file.isDirectory()) {
			File allFiles[] = file.listFiles();
			for (File file2 : allFiles) {
				recuperarFiles(file2, filesSalida);
			}
		} else {
			if (seDebeAgregarFile(file)) {
				filesSalida.add(file);
			} else {
				if (file.getName().endsWith(".zip")
						|| file.getName().endsWith(".rar")
						|| file.getName().endsWith(".jar")
						|| file.getName().endsWith(".ear")
						|| file.getName().endsWith(".war")) {
					descomprimirZip(file, filesSalida);
				}
			}
		}

	}

	public void documentar() throws IOException {
		// recorro todo los archivos dado un zip un file o un directorio/ por
		// ahora solo .java
		List<File> files = new ArrayList<File>();
		recuperarFiles(fileOrDirectory, files);

		// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// Try finding a compiler.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			// Normal JDK compiler not found, try finding one in classpath
			final ClassLoader loader = Documentador.class.getClassLoader();
			Class<JavaCompiler> cls;
			try {
				cls = (Class<JavaCompiler>) loader
						.loadClass("com.sun.tools.javac.api.JavacTool");
				compiler = cls.asSubclass(JavaCompiler.class).newInstance();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		File filesArray[] = new File[] {};
		filesArray = files.toArray(filesArray);
		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
				.getJavaFileObjects(filesArray);

		//CompilationTask task = compiler.getTask(null, fileManager, null, null,null, compilationUnits1);
		String[] options =  {"-Xmaxerrs","500"};
		CompilationTask task = compiler.getTask(null, fileManager, null , Arrays.asList(options) , null, compilationUnits1);
	
		// Get the list of annotation processors
		LinkedList<AbstractProcessor> processors = new LinkedList<AbstractProcessor>();
		analizador = new Analizador();
		processors.add(analizador);
		task.setProcessors(processors);
		// Perform the compilation task.

		task.call();

		try {
			Exportador.exportar(this.getSeparador(), this.getProyecto(),
					this.fileOrDirectory.getName(), this.analizador.beans, this.tabla1, this.tabla5);
			fileManager.close();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
