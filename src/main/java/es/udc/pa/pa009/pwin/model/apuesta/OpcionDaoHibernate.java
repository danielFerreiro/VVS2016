package es.udc.pa.pa009.pwin.model.apuesta;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("opcionDao")
public class OpcionDaoHibernate extends GenericDaoHibernate<Opcion, Long>
		implements OpcionDao {

	@Override
	public List<Opcion> showWinners(Long idTipoApuesta) {
		String query;

		query = "SELECT o FROM Opcion o WHERE o.tipoApuesta.idTipo = :id AND o.estado = TRUE";

		Query queryHQL = getSession().createQuery(query);
		queryHQL.setLong("id", idTipoApuesta);

		return queryHQL.list();
	}

}
