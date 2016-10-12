package es.udc.pa.pa009.pwin.model.apuesta;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@org.hibernate.annotations.BatchSize(size=10)
public class Opcion {

	private Long idOpcion;
	private String nombreOpcion;
	private Double cuota;
	private Boolean estado;
	private TipoApuesta tipoApuesta;
	private int version;
	
	public Opcion () {
		
	}
	
	public Opcion(String nombreOpcion, Double cuota, Boolean estado,TipoApuesta tipoApuesta) {
		super();
		this.nombreOpcion = nombreOpcion;
		this.cuota = cuota;
		this.estado = estado;
		this.tipoApuesta=tipoApuesta;
		tipoApuesta.addOpcion(this);
	}

	@SequenceGenerator(
			name  ="opcionIdGenerator",
			sequenceName = "opcionIdSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "opcionIdGenerator")
	public Long getIdOpcion() {
		return idOpcion;
	}

	public void setIdOpcion(Long idOpcion) {
		this.idOpcion = idOpcion;
	}

	public String getNombreOpcion() {
		return nombreOpcion;
	}

	public void setNombreOpcion(String nombreOpcion) {
		this.nombreOpcion = nombreOpcion;
	}

	public Double getCuota() {
		return cuota;
	}

	public void setCuota(Double cuota) {
		this.cuota = cuota;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	@JoinColumn(name="tipoApuesta")
	public TipoApuesta getTipoApuesta() {
		return tipoApuesta;
	}

	public void setTipoApuesta(TipoApuesta tipoApuesta) {
		this.tipoApuesta = tipoApuesta;
	}
	

	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		
		
		return "Opcion [idOpcion=" + idOpcion + ", nombreOpcion="
				+ nombreOpcion + ", cuota=" + cuota + ", estado=" + estado
				+ ", tipoApuesta=" + tipoApuesta.getPregunta() + ", version=" + version + "]";
	}
	
	
	
}
