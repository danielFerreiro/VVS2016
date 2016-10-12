package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("tipoApuestaDao")
public class TipoApuestaDaoHibernate extends
		GenericDaoHibernate<TipoApuesta, Long> implements TipoApuestaDao {

	@Override
	public boolean findDuplicateBetTypes(String question,Long idEvento) {

		String query;

		
		query = "SELECT t FROM TipoApuesta t WHERE t.pregunta LIKE :pregunta AND t.evento.idEvento = :idEvento";

		Query queryHQL = getSession().createQuery(query);
		
		queryHQL.setString("pregunta", question);
		queryHQL.setLong("idEvento", idEvento);
		
		List<TipoApuesta> tipos = queryHQL.list();

		return !tipos.isEmpty();
	}

}
