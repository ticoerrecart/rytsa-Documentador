package rytsa.documentador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
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
	protected static String extensionesAScanear;
	protected static String extensionesAInventariar;
	
	protected static ResourceBundle resourceBundle;
	
	List<File> files = new ArrayList<File>();

	static {
		resourceBundle = ResourceBundle.getBundle("config");
		extensionesAInventariar = resourceBundle.getString("extensionesAInventariar");
		extensionesAScanear = resourceBundle.getString("extensionesAScanear");
	}
	
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

	private void descomprimirZip(File file, List<File> filesSalida, String extensionDeArchivo)
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
				if (seDebeAgregarFile(file, extensionDeArchivo)) {
					filesSalida.add(file);
				}
			}
		}
	}

	private boolean seDebeAgregarFile(File file, String extensionDeArchivo) {
		boolean agregar = false;
		for (String ext : extensionDeArchivo.split(",")) {
			if ((file.getName().toLowerCase()).endsWith((ext.trim()).toLowerCase())){
				agregar = true;
			}
		}
		return agregar; 
	}

	private void recuperarFiles(File file, List<File> filesSalida, String extensionDeArchivo)
			throws IOException {
		if (file.isDirectory()) {
			File allFiles[] = file.listFiles();
			for (File file2 : allFiles) {
				recuperarFiles(file2, filesSalida, extensionDeArchivo );
			}
		} else {
			if (seDebeAgregarFile(file, extensionDeArchivo)) {
				filesSalida.add(file);
			} else {
				if (file.getName().endsWith(".zip")
						|| file.getName().endsWith(".rar")
						|| file.getName().endsWith(".jar")
						|| file.getName().endsWith(".ear")
						|| file.getName().endsWith(".war")) {
					descomprimirZip(file, filesSalida, extensionDeArchivo);
				}
			}
		}

	}

	public void documentar() throws IOException {
		// recorro todo los archivos dado un zip un file o un directorio/ por
		// ahora solo .java
		
		recuperarFiles(fileOrDirectory, files, ".java");

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
		fileManager.close();
		
		try {
			//Voy a ampliar la info de los beans, agregando los select, insert, delete como referencias y las tablas como objetos en el inventario
			buscoReferenciasSQL();
			
			ampliarDocumentacion(fileOrDirectory, files);
			
			Exportador.exportar(this.getSeparador(), this.getProyecto(),
					this.fileOrDirectory.getName(), this.analizador.beans, this.tabla1, this.tabla5);
			
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		files.clear();
	}
	
	// Creo nuevos beans con archivos que no son ".java"
	private void ampliarDocumentacion(File file, List<File> filesSalida){
		try {
			int cantFilesAnt = files.size();
			recuperarFiles(fileOrDirectory, files, this.extensionesAInventariar);
			int cantFilesAct = files.size();
			String codigoFuente = "";
			String codigoFuenteAux;
			
			//Por cada archivo (los no java) que se haya agregado a la documentación, tengo que obtener info
			List<File> salidaAmpliada = filesSalida.subList(cantFilesAnt, cantFilesAct);
			
			for (File file2 : salidaAmpliada) {
				ClaseBean beanNoJava = new ClaseBean();
				int ubicacionPunto = file2.getName().lastIndexOf(".");
								
				beanNoJava.setPaquete(file2.getParent());				
				beanNoJava.setNombre(file2.getName().substring(0, ubicacionPunto));
				beanNoJava.setTipo(file2.getName().substring(ubicacionPunto+1));
				beanNoJava.setSubtipo(file2.getName().substring(ubicacionPunto+1));
				beanNoJava.setDescripcion("");			
				
				// Si el archivo es de uno de los tipos a scanear, lee y guarda el código en el bean
				for (String ext : this.extensionesAScanear.split(",")) {
					if ((file2.getName().toLowerCase()).endsWith((ext.trim()).toLowerCase())){
					
						BufferedReader bf = new BufferedReader(new FileReader(file2));

						while ((codigoFuenteAux = bf.readLine())!=null) {
						   codigoFuente = codigoFuente + codigoFuenteAux;
						   // System.out.println(codigoFuenteAux);
						}
						beanNoJava.setCodigoFuente(codigoFuente);				
					}
				}				
				this.analizador.beans.add(beanNoJava);			
			}
			
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	private void buscoReferenciasSQL(){
		// 
		ArrayList<ClaseBean> beansSQL = new ArrayList<ClaseBean>(); //para inventariar las tablas
		
		//recorro la colección de beans para examinar su código
		for (ClaseBean bean : this.analizador.beans) {
			
			String codigo = (bean.getCodigoFuente()).toLowerCase();
			String desconocido = "desconocido";
			String objeto = "Tabla";
			
			for (String lineaCodigo : codigo.split(";")) {
				int ubicFrom = 0;
				int ubicProximoEspacio=0;
				String tabla = "";
				String cardinalidad = "";
//System.out.println(lineaCodigo);
				if (lineaCodigo.contains("select ")) {
					ubicFrom = lineaCodigo.indexOf("from ") + 5;
					ubicProximoEspacio = lineaCodigo.indexOf(" ", ubicFrom);
					if (ubicProximoEspacio == -1)
						ubicProximoEspacio = lineaCodigo.length();					
					tabla = lineaCodigo.substring(ubicFrom, ubicProximoEspacio);
					cardinalidad = "INP";
				}				
				if (lineaCodigo.contains("update ")) {
					ubicFrom = lineaCodigo.indexOf("update ")  + 7;
					ubicProximoEspacio = lineaCodigo.indexOf(" ", ubicFrom);
					if (ubicProximoEspacio == -1)
						ubicProximoEspacio = lineaCodigo.length();					
					tabla = lineaCodigo.substring(ubicFrom, ubicProximoEspacio);
					cardinalidad = "UPD";
				}
				if (lineaCodigo.contains("delete ")) {
					ubicFrom = lineaCodigo.indexOf("from ") + 5;
					ubicProximoEspacio = lineaCodigo.indexOf(" ", ubicFrom);
					if (ubicProximoEspacio == -1)
						ubicProximoEspacio = lineaCodigo.length();					
					tabla = lineaCodigo.substring(ubicFrom, ubicProximoEspacio);
					cardinalidad = "UPD";
					
				}
				if (lineaCodigo.contains("insert ")) {
					ubicFrom = lineaCodigo.indexOf("into ") + 5;
					ubicProximoEspacio = lineaCodigo.indexOf(" ", ubicFrom);
					if (ubicProximoEspacio == -1)
						ubicProximoEspacio = lineaCodigo.length();					
					tabla = lineaCodigo.substring(ubicFrom, ubicProximoEspacio);
					cardinalidad = "UPD";
				}
				if (! "".equals(tabla) && ! "+".equals(tabla) && ! tabla.equals(String.valueOf('"'))) {
					ClaseBean cb = new ClaseBean();
					cb.setNombre(tabla);
					cb.setCardinalidad(cardinalidad);
					cb.setPaquete("desconocido");
					cb.setTipo(objeto);
					cb.setSubtipo(objeto);
					cb.setDescripcion("");
					
					//	no estoy pudiendo detectar cuando el objeto está ya en el list para no insertarlo nuevamente					
					if (!beansSQL.contains(cb)) {
						beansSQL.add(cb);						
					}
					//	no estoy pudiendo detectar cuando el objeto está ya en el list para no insertarlo nuevamente
					if (!bean.getReferencias().contains(cb)) { 							
						bean.getReferencias().add(cb);
					}	
				}
			}			
		}
		// Agrego los objetos sql a la lista de beans
		this.analizador.beans.addAll(beansSQL);
	}
}
