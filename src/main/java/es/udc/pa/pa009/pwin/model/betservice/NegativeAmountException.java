package es.udc.pa.pa009.pwin.model.betservice;

import es.udc.pojo.modelutil.exceptions.InstanceException;

public class NegativeAmountException extends InstanceException {

	public NegativeAmountException(Object key, String className) {
		super(" Cantidad apostada negativa ", key, className);
	}
}
