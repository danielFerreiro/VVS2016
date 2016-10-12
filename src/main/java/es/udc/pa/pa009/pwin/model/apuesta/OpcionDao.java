package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import org.hibernate.Query;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface OpcionDao extends GenericDao<Opcion, Long>{
	
	public List<Opcion> showWinners (Long idTipoApuesta);
	
}
