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
				
				if (clases.contains(variable.getNombre())){
					fw2.append(proyecto)
					.append(separador).append(bean.getPaquete())
					.append(separador).append(bean.getNombre())
					.append(separador).append(bean.getTipo())
					.append(separador).append(bean.getSubtipo())
					.append(separador).append(obtenerPaquete(variable, beans))
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
					.append(separador).append(obtenerPaquete(claseEstatica, beans))  		
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

	// Tenemos algunas variables y clases estáticas que vienen con el paquete vacio.
	// Las voy a buscar entren los beans y voy a devolver el paquete correcto.
	private static String obtenerPaquete(ClaseBean claseBuscada, List<ClaseBean> clasesInventario){
		String paquete = "";
		if (claseBuscada.getPaquete() == null || claseBuscada.getPaquete().equals("")) {
			for (ClaseBean b : clasesInventario) {
				if (b.getNombre().equals(claseBuscada.getNombre())) {
					paquete = b.getPaquete();
				}
			}
		}
		return paquete;
	}


}


