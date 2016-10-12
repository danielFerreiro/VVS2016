package es.udc.pa.pa009.pwin.web.util;

import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.betservice.ApuestaBlock;
import es.udc.pa.pa009.pwin.model.betservice.BetService;

public class ApuestaGridDataSource implements GridDataSource{

	private final static int EVENTS_PER_PAGE = 10;
	
	
	private BetService betService;
	private Long userId;
	private int startIndex;
	private ApuestaBlock apuestaBlock;
	private List<Apuesta> apuestas;
	
	
	
	public ApuestaGridDataSource(Long userId,int startIndex,BetService b) {
		this.userId=userId;
		this.startIndex=startIndex;
		betService = b;
	}
	
	@Override
	public int getAvailableRows() {
		return betService.getNumberOfBets(userId);
	}

	@Override
	public void prepare(int startIndex, int endIndex,
			List<SortConstraint> sortConstraints) {
		
		apuestaBlock = betService.checkBet(userId, startIndex, EVENTS_PER_PAGE+1);
		
		apuestas = apuestaBlock.getApuestas();
		this.startIndex=startIndex;
		
	}

	@Override
	public Object getRowValue(int index) {
		return apuestas.get(index-startIndex);
	}

	@Override
	public Class getRowType() {
		return Apuesta.class;
	}

	public List<Apuesta> getApuestas() {
		return apuestas;
	}
	
	public void setApuestas(List<Apuesta> apuestas) {
		this.apuestas=apuestas;
	}
	
	
	
}
