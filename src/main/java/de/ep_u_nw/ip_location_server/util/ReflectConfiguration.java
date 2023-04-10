package de.ep_u_nw.ip_location_server.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.ep_u_nw.ip_location_server.Configuration;

public class ReflectConfiguration {
    public static void main(String[] args) {
        Class<?> variable = Configuration.Variable.class;
        List<String> fields = new ArrayList<String>();
        for (Field field : Configuration.class.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) && variable.isAssignableFrom(field.getType())) {
                fields.add(field.getName());
            }
        }
        System.out.println(fields);
    }
}
