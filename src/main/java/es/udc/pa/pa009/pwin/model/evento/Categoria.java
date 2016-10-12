package es.udc.pa.pa009.pwin.model.evento;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.BatchSize;

@Entity
@Immutable
@org.hibernate.annotations.BatchSize(size=10)
public class Categoria {

	private Long idCategoria;
	private String nombreCategoria;
	
	public Categoria() {
		
	}
	
	public Categoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}

	@SequenceGenerator(
			name  ="categoriaIdGenerator",
			sequenceName = "categoriaIdSeq")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "categoriaIdGenerator")
	public Long getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getNombreCategoria() {
		return nombreCategoria;
	}

	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}

	@Override
	public String toString() {
		return "Categoria [idCategoria=" + idCategoria + ", nombreCategoria="
				+ nombreCategoria + "]";
	}
	
	
	
	
}
