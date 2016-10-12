package es.udc.pa.pa009.pwin.model.adminservice;

import es.udc.pojo.modelutil.exceptions.InstanceException;

public class IllegalParameterException extends InstanceException{

	public IllegalParameterException(Object key,String className) {
		super("Illegal argument ",key,className);
	}
}
