package rytsa.documentador;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Exportador {

	public static void exportar(String separador, String proyecto, String residencia,
			List<ClaseBean> beans) throws IOException {
		FileWriter fw = new FileWriter("Tabla1.txt");
		
		for (ClaseBean bean : beans) {
			fw.append(proyecto)
			.append(separador).append(residencia)
			.append(separador).append(bean.getPaquete())
			.append(separador).append(bean.getNombre())
			.append(separador).append(bean.getTipo())
			.append(separador).append(bean.getSubtipo())
			.append(separador).append(bean.getDescripcion())
			.append("\n");	
		} 
		
		
		fw.append("*FIN");
		fw.flush();
		fw.close();
	}

}
