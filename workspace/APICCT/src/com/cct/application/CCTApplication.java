package com.cct.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.cct.resources.Requerimiento;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services/*")
public class CCTApplication extends Application {
    public Set<Class<?>> getClasses() {
    	
        return new HashSet<Class<?>>(Arrays.asList(Requerimiento.class));
    }
}
