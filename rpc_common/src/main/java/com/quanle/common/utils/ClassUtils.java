package com.quanle.common.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author quanle
 * @date 2020/5/6 10:40 PM
 */
public class ClassUtils {
    /**
     * 根据注解获取类
     *
     * @param annotationClass 注解
     * @return
     */
    public static List<Class<?>> getClassByAnnotation(Class<? extends Annotation> annotationClass, String packageName) {
        List<Class<?>> returnClassList = new ArrayList<>();
        try {
            List<Class<?>> allClass = getClasses(packageName);
            for (Class<?> clazz : allClass) {
                if (clazz.isAnnotationPresent(annotationClass)) {
                    returnClassList.add(clazz);
                }
            }
        } catch (Exception ignored) {
        }
        return returnClassList;
    }

    /**
     * 根据包名获得该包以及子包下的所有类不查找jar包中的
     *
     * @param packageName 包名
     * @return List<Class>    包下所有类
     */
    private static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String newPath = resource.getFile().replace("%20", " ");
            dirs.add(new File(newPath));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClass(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
