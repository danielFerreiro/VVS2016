package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.Calendar;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;

@Entity
@Immutable
public class Apuesta {

	private Long idApuesta;
	private Calendar fecha;
	private UserProfile usuario;
	private Double apostado;
	private Opcion opcionElegida;
	
	public Apuesta() {
		
	}

	public Apuesta(Calendar fecha, UserProfile usuario, Double apostado,Opcion opcionElegida) {
		if(fecha!=null){
			fecha.set(Calendar.MILLISECOND, 0);
		}
		this.fecha = fecha;
		this.usuario = usuario;
		this.apostado = apostado;
		this.opcionElegida = opcionElegida;
	}

	@SequenceGenerator(
			name  ="apuestaIdGenerator",
			sequenceName = "apuestaIdSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "apuestaIdGenerator")
	public Long getIdApuesta() {
		return idApuesta;
	}

	public void setIdApuesta(Long idApuesta) {
		this.idApuesta = idApuesta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getFecha() {
		return fecha;
	}

	public void setFecha(Calendar fecha) {
		fecha.set(Calendar.MILLISECOND, 0);
		this.fecha = fecha;
	}

	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	@JoinColumn(name="usuario")
	public UserProfile getUsuario() {
		return usuario;
	}

	public void setUsuario(UserProfile usuario) {
		this.usuario = usuario;
	}

	public Double getApostado() {
		return apostado;
	}

	public void setApostado(Double apostado) {
		this.apostado = apostado;
	}

	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	@JoinColumn(name="opcionElegida")
	public Opcion getOpcionElegida() {
		return opcionElegida;
	}

	public void setOpcionElegida(Opcion opcionElegida) {
		this.opcionElegida = opcionElegida;
	}

	@Override
	public String toString() {
		return "Apuesta [idApuesta=" + idApuesta + ", fecha=" + fecha
				+ ", usuario=" + usuario + ", apostado=" + apostado
				+ ", opcionElegida=" + opcionElegida + "]";
	}
	
	
	
	
}
