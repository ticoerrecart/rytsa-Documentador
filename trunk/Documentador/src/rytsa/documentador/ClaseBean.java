package rytsa.documentador;

import java.util.ArrayList;
import java.util.List;

public class ClaseBean {

	private String nombre;
	private String nombreInstancia;
	private String paquete;
	private String tipo;
	private String subtipo;
	private String descripcion; 
	private String cardinalidad;
	private List<ClaseBean> variables = new ArrayList<ClaseBean>();
	

	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getSubtipo() {
		return subtipo;
	}
	public void setSubtipo(String subtipo) {
		this.subtipo = subtipo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPaquete() {
		return paquete;
	}
	public void setPaquete(String paquete) {
		this.paquete = paquete;
	}
	public String getNombreInstancia() {
		return nombreInstancia;
	}
	public void setNombreInstancia(String nombreInstancia) {
		this.nombreInstancia = nombreInstancia;
	}
	public void setVariables(List<ClaseBean> variables) {
		this.variables = variables;
	}
	public List<ClaseBean> getVariables() {
		return variables;
	}
	public String getCardinalidad() {
		return cardinalidad;
	}
	public void setCardinalidad(String cardinalidad) {
		this.cardinalidad = cardinalidad;
	}
	
	
}
