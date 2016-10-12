package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface TipoApuestaDao extends GenericDao<TipoApuesta, Long>{
	
	public boolean findDuplicateBetTypes(String question,Long idEvento);
}
