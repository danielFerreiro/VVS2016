package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import es.udc.pa.pa009.pwin.model.evento.Evento;

@Entity
@org.hibernate.annotations.BatchSize(size=10)
public class TipoApuesta {

	private Long idTipo;
	private String pregunta;
	private List<Opcion> opciones;
	private Evento evento;
	private boolean multiple;

	public TipoApuesta() {

	}

	public TipoApuesta(Evento evento, String pregunta, boolean multiple) {
		super();
		this.pregunta = pregunta;
		this.multiple = multiple;
		this.evento = evento;
		opciones = new ArrayList<Opcion>();
		evento.getTipos().add(this);

	}

	@SequenceGenerator(name = "tipoApuestaIdGenerator", sequenceName = "tipoApuestaIdSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tipoApuestaIdGenerator")
	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	@OneToMany(mappedBy = "tipoApuesta")
	public List<Opcion> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<Opcion> opciones) {
		this.opciones = opciones;
	}
	
	public void addOpcion(Opcion op) {
		opciones.add(op);
		op.setTipoApuesta(this);
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "evento")
	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}
	

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@Override
	public String toString() {
		return "TipoApuesta [idTipo=" + idTipo + ", pregunta=" + pregunta
				+ ", opciones=" + opciones + ", evento=" + evento
				+ ", multiple=" + multiple + "]";
	}

}
