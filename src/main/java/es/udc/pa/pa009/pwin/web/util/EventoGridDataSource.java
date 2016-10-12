package es.udc.pa.pa009.pwin.web.util;

import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

public class EventoGridDataSource implements GridDataSource {

	private final static int EVENTS_PER_PAGE = 10;
	private Long categoryId;
	private String keywords;
	private int startIndex;
	private Evento evento;
	private EventoBlock eventoBlock;
	private BetService betService;
	private List<Evento> listaEventos;

	public EventoGridDataSource(Long categoryId, String keywords,
			BetService betService) {
		super();
		this.categoryId = categoryId;
		this.keywords = keywords;
		this.betService = betService;
	}
	
	
	public List<Evento> getListaEventos() {
		return listaEventos;
	}


	public void setListaEventos(List<Evento> listaEventos) {
		this.listaEventos = listaEventos;
	}


	@Override
	public int getAvailableRows() {
		return betService.getNumberOfEvents(keywords, categoryId);
		
	}

	@Override
	public Class<Evento> getRowType() {
		return Evento.class;
	}

	@Override
	public Object getRowValue(int index) {
		
		return listaEventos.get(index-(startIndex));
	}

	@Override
	public void prepare(int startIndex, int endIndex,
			List<SortConstraint> sortConstraints) {
		eventoBlock = betService.findEvent(categoryId, keywords, startIndex,
				endIndex + 1);
		listaEventos = eventoBlock.getEventos();
		
		this.startIndex = startIndex;

	}

}
