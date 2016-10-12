package es.udc.pa.pa009.pwin.model.evento;

import es.udc.pojo.modelutil.exceptions.InstanceException;

public class ExpiredEventException extends InstanceException{

	public ExpiredEventException(Object key,String className) {
		super(" Evento ya expirado ",key,className);
	}
	
	
}
