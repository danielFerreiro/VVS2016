package es.udc.pa.pa009.pwin.model.betservice;

import java.util.List;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;

public class ApuestaBlock {

	private List<Apuesta> apuestas;
	private boolean existMoreBets;
	
	public ApuestaBlock() {
		
	}

	public ApuestaBlock(List<Apuesta> apuestas, boolean existMoreBets) {
		this.apuestas = apuestas;
		this.existMoreBets = existMoreBets;
	}

	public List<Apuesta> getApuestas() {
		return apuestas;
	}

	public void setApuestas(List<Apuesta> apuestas) {
		this.apuestas = apuestas;
	}

	public boolean isExistMoreBets() {
		return existMoreBets;
	}

	public void setExistMoreBets(boolean existMoreBets) {
		this.existMoreBets = existMoreBets;
	}
	
	

	
	
}
