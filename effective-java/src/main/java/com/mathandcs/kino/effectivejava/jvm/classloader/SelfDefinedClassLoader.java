package com.mathandcs.kino.effectivejava.jvm.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by dashwang on 11/28/17.
 * <p>
 * ref: http://www.jianshu.com/p/a8371d26f848
 */
public class SelfDefinedClassLoader extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfDefinedClassLoader.class);

    public static final String classPath = "./effective-java/target/classes/com/mathandcs/kino/effectivejava/jvm/classloader/";
    public static final String fileType = ".class";

    public Class findClass(String className) {
        byte[] data = loadClassData(className);
        Class clazz = defineClass(data, 0, data.length);
        return clazz;
    }

    private byte[] loadClassData(String className) {
        FileInputStream fis = null;
        byte[] data = null;
        try {
            File file = new File(classPath + className + fileType);
            LOGGER.info("Load class data from {}", file.getAbsolutePath());
            fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ch = 0;
            while((ch = fis.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } catch(IOException e) {
            LOGGER.error("Failed to load data of class: {} from path: {}, exception: {}", className, classPath, e);
            throw new RuntimeException(e);
        }
        return data;
    }

    public static void main(String[] args) {
        LOGGER.info("Current working dir: {}", System.getProperty("user.dir"));
        SelfDefinedClassLoader selfDefinedClassLoader = new SelfDefinedClassLoader();
        selfDefinedClassLoader.findClass("SelfDefinedClassLoader");
    }
}
