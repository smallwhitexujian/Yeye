package com.angelatech.yeyelive1.db;

import java.lang.reflect.Type;

/**
 * Created by jjfly on 16-5-27.
 */
public class ReflectUtil {

    private static final String TYPE_NAME_PREFIX = "class ";

    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

}
