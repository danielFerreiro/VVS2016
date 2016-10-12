package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pojo.modelutil.dao.GenericDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

public interface ApuestaDao extends GenericDao<Apuesta, Long>{

	public List<Apuesta> findApuestasByIdUsuario(Long userId,
			int startIndex, int count);
	
	public int findNumberOfBets(Long userID);
}
