package org.jboss.uberfire.poc.client.local;


import javax.annotation.PostConstruct;

import org.jboss.errai.enterprise.client.jaxrs.api.RestClient;
import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * This is the entry point to the client portion of the web application. At
 * compile time, Errai finds the {@code @EntryPoint} annotation on this class
 * and generates bootstrap code that creates an instance of this class when the
 * page loads. This client-side bootstrap code will also call the
 * {@link #init()} method because it is annotated with the
 * {@code @PostConstruct} annotation.
 */
@EntryPoint
public class App {


}