package es.udc.pa.pa009.pwin.model.evento;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("eventoDao")
public class EventoDaoHibernate extends GenericDaoHibernate<Evento, Long>
		implements EventoDao {

	@SuppressWarnings("unchecked")
	public List<Evento> findEvent(Long idCategoria, String keywords,
			int startIndex, int count) {
		// Aqui se van a buscar los eventos por keywords y categoria
		String query;
		// se separan las keywords
		String[] arrayWords = keywords != null ? keywords.split(" ") : null;

		// busqueda de las palabras clave
		if (arrayWords != null) {
			query = "SELECT e FROM Evento e WHERE (UNIX_TIMESTAMP(e.fecha) > UNIX_TIMESTAMP(CURRENT_DATE())) AND UPPER(e.nombreEvento) LIKE :nombreEvento";
			for (Integer i = 0; i < arrayWords.length - 1; i++) {
				query = query + " OR UPPER(e.nombreEvento) LIKE :nombreEvento"
						+ i.toString();
			}
			query = query + " AND ";
		} else {
			query = "SELECT e FROM Evento e WHERE (UNIX_TIMESTAMP(e.fecha) > UNIX_TIMESTAMP(CURRENT_DATE())) AND";
		}

		// busqueda de la categoria
		if (idCategoria != null) {
			query = query + " e.categoria.idCategoria = :idCategoria";
		} else {
			// clasico 1=1 para que el AND de arriba no haga que pete todo
			query = query + " 1=1";
		}

		// se ordena por fecha
		query = query
				+ " ORDER BY (UNIX_TIMESTAMP(e.fecha) - UNIX_TIMESTAMP(CURRENT_DATE())) ASC";
		Query queryHQL = getSession().createQuery(query);

		// a partir de aqui se cargan las cosas mediante las cuales se hará la
		// búsqueda
		if (arrayWords != null) {
			queryHQL.setParameter("nombreEvento",
					"%" + arrayWords[0].toUpperCase() + "%");
			for (Integer i = 0; i < arrayWords.length - 1; i++) {
				queryHQL.setParameter("nombreEvento" + i.toString(), "%"
						+ arrayWords[i + 1].toUpperCase() + "%");
			}
		}

		if (idCategoria != null) {
			queryHQL.setParameter("idCategoria", idCategoria);
		}

		return queryHQL.setFirstResult(startIndex).setMaxResults(count).list();
	}

	@SuppressWarnings("unchecked")
	public List<Evento> findEventNoDate(Long idCategoria, String keywords,
			int startIndex, int count) {
		String query;
		String[] arrayWords = keywords != null ? keywords.split(" ") : null;

		if (arrayWords != null) {
			query = "SELECT e FROM Evento e WHERE UPPER(e.nombreEvento) LIKE :nombreEvento";
			for (Integer i = 0; i < arrayWords.length - 1; i++) {
				query = query + " OR UPPER(e.nombreEvento) LIKE :nombreEvento"
						+ i.toString();
			}
			query = query + " AND ";
		} else {
			query = "SELECT e FROM Evento e WHERE ";
		}

		if (idCategoria != null) {
			query = query + "e.categoria.idCategoria = :idCategoria";

		} else
			query = query + "1=1";
		
		query = query
				+ " ORDER BY (UNIX_TIMESTAMP(e.fecha) - UNIX_TIMESTAMP(CURRENT_DATE())) ASC";

		Query queryHQL = getSession().createQuery(query);
		// insertamos datos
		if (arrayWords != null) {
			queryHQL.setParameter("nombreEvento",
					"%" + arrayWords[0].toUpperCase() + "%");
			for (Integer i = 0; i < arrayWords.length - 1; i++) {
				queryHQL.setParameter("nombreEvento" + i.toString(), "%"
						+ arrayWords[i + 1].toUpperCase() + "%");
			}
		}

		if (idCategoria != null) {
			queryHQL.setParameter("idCategoria", idCategoria);
		}

		return queryHQL.setFirstResult(startIndex).setMaxResults(count).list();
	}

	@Override
	public boolean findDuplicateEvents(String eventName, Calendar date,
			Long idCategoria) {

		String query;

		query = "SELECT e FROM Evento e WHERE UPPER(e.nombreEvento) LIKE UPPER(:nombreEvento) AND "
				+ "UNIX_TIMESTAMP(e.fecha) = UNIX_TIMESTAMP(:fechaActual) "
				+ "AND e.categoria.idCategoria = :idCategoria";

		Query queryHQL = getSession().createQuery(query);

		queryHQL.setString("nombreEvento", eventName);
		queryHQL.setTimestamp("fechaActual", date.getTime());
		queryHQL.setLong("idCategoria", idCategoria);

		List<Evento> eventosEncontrados = queryHQL.list();

		return !eventosEncontrados.isEmpty();

	}

	@Override
	public int getNumberOfEvents(Long categoryId, String keywords) {
		// Aqui se van a buscar los eventos por keywords y categoria
				String query;
				// se separan las keywords
				String[] arrayWords = keywords != null ? keywords.split(" ") : null;

				// busqueda de las palabras clave
				if (arrayWords != null) {
					query = "SELECT e FROM Evento e WHERE (UNIX_TIMESTAMP(e.fecha) > UNIX_TIMESTAMP(CURRENT_DATE())) AND UPPER(e.nombreEvento) LIKE :nombreEvento";
					for (Integer i = 0; i < arrayWords.length - 1; i++) {
						query = query + " OR UPPER(e.nombreEvento) LIKE :nombreEvento"
								+ i.toString();
					}
					query = query + " AND ";
				} else {
					query = "SELECT e FROM Evento e WHERE (UNIX_TIMESTAMP(e.fecha) > UNIX_TIMESTAMP(CURRENT_DATE())) AND";
				}

				// busqueda de la categoria
				if (categoryId != null) {
					query = query + " e.categoria.idCategoria = :idCategoria";
				} else {
					// clasico 1=1 para que el AND de arriba no haga que pete todo
					query = query + " 1=1";
				}

				// se ordena por fecha
				query = query
						+ " ORDER BY (UNIX_TIMESTAMP(e.fecha) - UNIX_TIMESTAMP(CURRENT_DATE())) ASC";
				Query queryHQL = getSession().createQuery(query);

				// a partir de aqui se cargan las cosas mediante las cuales se hará la
				// búsqueda
				if (arrayWords != null) {
					queryHQL.setParameter("nombreEvento",
							"%" + arrayWords[0].toUpperCase() + "%");
					for (Integer i = 0; i < arrayWords.length - 1; i++) {
						queryHQL.setParameter("nombreEvento" + i.toString(), "%"
								+ arrayWords[i + 1].toUpperCase() + "%");
					}
				}

				if (categoryId != null) {
					queryHQL.setParameter("idCategoria", categoryId);
				}

				return queryHQL.list().size();
	}
	
	
	public int getNumberOfEvents2(Long categoryId, String keywords) {
		// Aqui se van a buscar los eventos por keywords y categoria
				String query;
				// se separan las keywords
				String[] arrayWords = keywords != null ? keywords.split(" ") : null;

				// busqueda de las palabras clave
				if (arrayWords != null) {
					query = "SELECT e FROM Evento e WHERE UPPER(e.nombreEvento) LIKE :nombreEvento";
					for (Integer i = 0; i < arrayWords.length - 1; i++) {
						query = query + " OR UPPER(e.nombreEvento) LIKE :nombreEvento"
								+ i.toString();
					}
					query = query + " AND ";
				} else {
					query = "SELECT e FROM Evento e WHERE";
				}

				// busqueda de la categoria
				if (categoryId != null) {
					query = query + " e.categoria.idCategoria = :idCategoria";
				} else {
					// clasico 1=1 para que el AND de arriba no haga que pete todo
					query = query + " 1=1";
				}

				// se ordena por fecha
				query = query
						+ " ORDER BY (UNIX_TIMESTAMP(e.fecha) - UNIX_TIMESTAMP(CURRENT_DATE())) ASC";
				Query queryHQL = getSession().createQuery(query);

				// a partir de aqui se cargan las cosas mediante las cuales se hará la
				// búsqueda
				if (arrayWords != null) {
					queryHQL.setParameter("nombreEvento",
							"%" + arrayWords[0].toUpperCase() + "%");
					for (Integer i = 0; i < arrayWords.length - 1; i++) {
						queryHQL.setParameter("nombreEvento" + i.toString(), "%"
								+ arrayWords[i + 1].toUpperCase() + "%");
					}
				}

				if (categoryId != null) {
					queryHQL.setParameter("idCategoria", categoryId);
				}

				return queryHQL.list().size();
	}

	@Override
	public List<Evento> findAllEvents(Long idCategoria, String keywords) {
		String query = "SELECT e FROM Evento e WHERE (UNIX_TIMESTAMP(e.fecha) > UNIX_TIMESTAMP(CURRENT_DATE()))";
		String [] clave = keywords != null ? clave = keywords.split(" ") : null;
		int i = 1;
		
		if (idCategoria!=null) {
			query  =query+" AND e.categoria.idCategoria = :categoria";
		}
		
		if (clave != null) {
			query = query+" AND UPPER(e.nombreEvento) LIKE :nombreEvento0";
			
			while (i<clave.length) {
				query = query+" OR UPPER(e.nombreEvento) LIKE :nombreEvento"+i;
				i++;
			}
			
		}
		
		query = query + " ORDER BY e.nombreEvento";
		
		Query queryHQL = getSession().createQuery(query);
		
		if (idCategoria != null)
			queryHQL.setLong("categoria",idCategoria);
		
		
		
		if (clave!=null) {
			i = 0;
			while (i<clave.length) {
				queryHQL.setString("nombreEvento"+i, "%"+clave[i]+"%");
				i++;
			}
		}

		return queryHQL.list();
		
	}

	
	
	@Override
	public List<Evento> findAllEventsNoDate(Long idCategoria, String keywords) {
		
		String query = "SELECT e FROM Evento e";
		String [] clave = keywords != null ? clave = keywords.split(" ") : null;
		int i = 1;
		
		
		if (idCategoria != null) {
			query=query+" WHERE e.categoria.idCategoria = :categoria";
		}
		
		if (clave != null) {
			
			if (idCategoria != null)
				query = query+" AND UPPER(e.nombreEvento) LIKE :nombreEvento0";
			else
				query = query+" WHERE UPPER(e.nombreEvento) LIKE :nombreEvento0";
			
			
			while (i<clave.length) {
				
				query = query+" OR UPPER(e.nombreEvento) LIKE :nombreEvento"+i;
				i++;
			}
			
		}
		 
		query = query+" ORDER BY e.nombreEvento";
		
		Query queryHQL = getSession().createQuery(query);
		
		if (idCategoria != null) {
			queryHQL.setLong("categoria", idCategoria);
		}
		
		if (clave != null) {
			queryHQL.setParameter("nombreEvento0", "%"+clave[0]+"%");
			
			i = 1;
			
			while (i<clave.length) {
				queryHQL.setParameter("nombreEvento"+i, "%"+clave[i]+"%" );
				i++;
			}
			
			
		}
		
		
		
		return queryHQL.list();
		
	}
	
	
}
