package es.udc.pa.pa009.pwin.model.adminservice;

import es.udc.pojo.modelutil.exceptions.InstanceException;

public class InvalidEventDateException extends InstanceException{

	public InvalidEventDateException(Object key,String className) {
		super("Invalid event date ",key,className);
	}
}
