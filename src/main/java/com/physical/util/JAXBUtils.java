package com.physical.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBUtils<T> {
	JAXBContext context;
	public JAXBUtils(Class<?> clazz){
		try {
			 
			context=JAXBContext.newInstance(clazz);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
 
/**
 * Bean to xml
 */
public String getXMl(T object){
	try{
		Marshaller marshaller=context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		Writer w=new StringWriter();
		marshaller.marshal(object, w);
		return w.toString();
	}catch(JAXBException e){
		throw new RuntimeException(e);
	}
}
	
/**
 * Xml to bean
 * @param xmlStr
 * @return
 */
  @SuppressWarnings("unchecked")
  public T getObject(String xmlStr){
	  try {
			Unmarshaller um=context.createUnmarshaller();
			ByteArrayInputStream in=new ByteArrayInputStream(xmlStr.getBytes());
			return (T)um.unmarshal(in);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
  }
}
