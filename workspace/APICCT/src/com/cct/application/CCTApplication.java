package com.cct.application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.cct.resources.Requerimiento;


public class CCTApplication extends Application {
    public Set<Class<?>> getClasses() {
    	
        return new HashSet<Class<?>>(Arrays.asList(Requerimiento.class));
    }
}
