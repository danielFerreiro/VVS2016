package es.udc.pa.pa009.pwin.model.evento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.tapestry5.beaneditor.NonVisual;

import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;

@Entity
@org.hibernate.annotations.BatchSize(size=10)
public class Evento {

	
	private Long idEvento;
	private String nombreEvento;
	private Calendar fecha;
	private Categoria categoria;
	private List<TipoApuesta> tipos;

	public Evento() {

	}

	public Evento(String nombreEvento, Calendar fecha, Categoria categoria) {
		this.nombreEvento = nombreEvento;
		if(fecha!=null){
			fecha.set(Calendar.MILLISECOND, 0);
		}
		this.fecha = fecha;
		this.categoria = categoria;
		tipos = new ArrayList<TipoApuesta>();
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
	}

	@SequenceGenerator(name = "eventoIdGenerator", sequenceName = "eventoIdSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "eventoIdGenerator")
	public Long getIdEvento() {
		return idEvento;
	}

	public String getNombreEvento() {
		return nombreEvento;
	}

	public void setNombreEvento(String nombre) {
		nombreEvento = nombre;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getFecha() {
		return fecha;
	}

	public void setFecha(Calendar fecha) {
		fecha.set(Calendar.MILLISECOND, 0);
		this.fecha = fecha;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria")
	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@OneToMany(mappedBy = "evento")
	public List<TipoApuesta> getTipos() {
		return tipos;
	}

	public void setTipos(List<TipoApuesta> tipos) {
		this.tipos = tipos;

	}

	public void addTipo(TipoApuesta tipo) {
		tipos.add(tipo);
		tipo.setEvento(this);
	}

	@Override
	public String toString() {

		String evento = "Evento [ idEvento = " + idEvento + ", nombreEvento= "
				+ nombreEvento + ", fecha =";

		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		evento = evento + formato.format(fecha.getTime()) + ", categoria= "
				+ categoria + ", tipos=";

		for (TipoApuesta t : tipos) {
			evento = evento + t.getPregunta() + " / ";
		}

		return evento;
	}

}
