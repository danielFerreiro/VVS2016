package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pojo.modelutil.dao.GenericDaoHibernate;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@SuppressWarnings("unchecked")
@Repository("apuestaDao")
public class ApuestaDaoHibernate extends GenericDaoHibernate<Apuesta, Long>
		implements ApuestaDao {

	public List<Apuesta> findApuestasByIdUsuario(Long userId,
			int startIndex, int count) {

		String query;

		query = "SELECT a FROM Apuesta a WHERE a.usuario.userProfileId = :id ORDER BY a.fecha";
		
		Query queryHQL = getSession().createQuery(query);
		queryHQL.setLong("id", userId);

		return queryHQL.setFirstResult(startIndex).setMaxResults(count)
				.list();

	}

	public int findNumberOfBets(Long userID) {
		
		String query;
		
		query = "SELECT COUNT(a) FROM Apuesta a WHERE a.usuario.userProfileId = :usuario";
		
		Query queryHQL = getSession().createQuery(query);
		queryHQL.setLong("usuario", userID);
		
		return Integer.parseInt( Long.toString( (Long) queryHQL.uniqueResult() ) );
		
	}
	
	
}
