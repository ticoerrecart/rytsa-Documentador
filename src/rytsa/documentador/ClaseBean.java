package rytsa.documentador;

public class ClaseBean {

	private String nombre;
	private String paquete;
	private String tipo;
	private String subtipo;
	private String descripcion; 
	
	
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
	
}
