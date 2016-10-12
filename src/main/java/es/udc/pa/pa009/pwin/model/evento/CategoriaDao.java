package es.udc.pa.pa009.pwin.model.evento;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface CategoriaDao extends GenericDao<Categoria,Long>{

	public List<Categoria> findCategories();
	
}
