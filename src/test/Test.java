package test;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			CodeAnalyzerController controller = new CodeAnalyzerController();
			controller.invokeProcessor(
					"E:\\DESARROLLO\\Workspace\\Fabiana2\\src\\rytsa\\Archivos.java,E:\\DESARROLLO\\Workspace\\Fabiana2\\src\\rytsa\\Archivo.java,E:\\DESARROLLO\\Workspace\\Fabiana2\\src\\rytsa\\PropertyFileFilter.java"
					);
		}

}
