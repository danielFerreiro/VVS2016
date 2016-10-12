package es.udc.pa.pa009.pwin.model.evento;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("categoriaDao")
public class CategoriaDaoHibernate extends GenericDaoHibernate<Categoria, Long>
		implements CategoriaDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Categoria> findCategories() {
			
		String query = "SELECT c"+
						" FROM Categoria c"+
						" ORDER BY c.idCategoria";

		Query queryHQL = getSession().createQuery(query);
		
		return queryHQL.list();
		
	}

	
	
}
