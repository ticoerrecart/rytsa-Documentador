package rytsa.documentador;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
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
		
		FileWriter fw2 = new FileWriter("Tabla5.txt");
		for (ClaseBean bean : beans) {
			int i = 0;
			for (ClaseBean variable : bean.getVariables()) {
				fw2.append(proyecto)
				.append(separador).append(residencia)
				.append(separador).append(bean.getPaquete())
				.append(separador).append(bean.getNombre())
				.append(separador).append(bean.getTipo())
				.append(separador).append(bean.getSubtipo())
				.append(separador).append(variable.getPaquete())
				.append(separador).append(variable.getNombreInstancia())
				.append(separador).append(variable.getNombre())
				.append(separador).append(variable.getSubtipo())
				.append(separador).append(variable.getCardinalidad())
				.append(separador).append(String.valueOf(i++))
				.append("\n");	
			}
		} 
		
		
		fw.append("*FIN");
		fw2.append("*FIN");
		fw.flush();
		fw2.flush();
		fw.close();
		fw2.close();
	}

}


