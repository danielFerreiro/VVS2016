package es.udc.pa.pa009.pwin.model.adminservice;

import es.udc.pojo.modelutil.exceptions.InstanceException;


public class NotMultipleOptionsException extends InstanceException{
	
	 public NotMultipleOptionsException(Object key, String className) {
	        super("Not multiple options", key, className);
	 }
}
