package rytsa.documentador;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exportador {

	public static void exportar(String separador, String proyecto, String residencia,
			List<ClaseBean> beans) throws IOException {
		FileWriter fw = new FileWriter("Tabla1.txt");
		List<String> clases = new ArrayList<String>();
		for (ClaseBean bean : beans) {
			fw.append(proyecto)
			.append(separador).append(residencia)
			.append(separador).append(bean.getPaquete())
			.append(separador).append(bean.getNombre())
			.append(separador).append(bean.getTipo())
			.append(separador).append(bean.getSubtipo())
			.append(separador).append(bean.getDescripcion())
			.append("\n");	
			clases.add(bean.getNombre());
		} 
		
		FileWriter fw2 = new FileWriter("Tabla5.txt");
		for (ClaseBean bean : beans) {
			int i = 0;
			
			for (ClaseBean variable : bean.getVariables()) {
				//si variable.getNombre() esta en beans.bean.getNombre().
				
				if (clases.contains(variable.getNombre())){
					fw2.append(proyecto)
					//.append(separador).append(residencia)
					.append(separador).append(bean.getPaquete())
					.append(separador).append(bean.getNombre())
					.append(separador).append(bean.getTipo())
					.append(separador).append(bean.getSubtipo())
					.append(separador).append(variable.getPaquete())
					.append(separador).append(variable.getNombre())
					.append(separador).append(variable.getTipo())
					.append(separador).append(variable.getSubtipo())
					.append(separador).append(variable.getCardinalidad())
					.append(separador).append(String.valueOf(i++))
					.append("\n");	
				}
			}


			for (ClaseBean claseEstatica : bean.getClasesEstaticas()) {
								
				if (clases.contains(claseEstatica.getNombre())){					
					
					fw2.append(proyecto)
					.append(separador).append(bean.getPaquete())
					.append(separador).append(bean.getNombre())
					.append(separador).append(bean.getTipo())
					.append(separador).append(bean.getSubtipo())
					.append(separador).append("") //todavía no tenemos el paquete  .append(separador).append(bean.getPaquete()) 		
					.append(separador).append(claseEstatica.getNombre())
					.append(separador).append(claseEstatica.getTipo())
					.append(separador).append(claseEstatica.getSubtipo())
					.append(separador).append(claseEstatica.getCardinalidad())
					.append(separador).append(String.valueOf(i++))
					.append("\n");	
				}
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


