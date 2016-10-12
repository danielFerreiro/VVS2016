package es.udc.pa.pa009.pwin.model.adminservice;

import es.udc.pojo.modelutil.exceptions.InstanceException;

public class AlreadySetOptionException extends InstanceException{

	 public AlreadySetOptionException(Object key, String className) {
	        super("Already set option", key, className);
	 }
	
}
