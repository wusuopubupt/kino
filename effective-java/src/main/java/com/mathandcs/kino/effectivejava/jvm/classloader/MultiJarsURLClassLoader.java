package com.mathandcs.kino.effectivejava.jvm.classloader;

import com.mathandcs.kino.effectivejava.serviceloader.MyPrintService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by dashwang on 12/7/17.
 *
 * 自定义类加载器, 加载指定路径下的多个jar文件
 */
public class MultiJarsURLClassLoader extends URLClassLoader {

    public MultiJarsURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public MultiJarsURLClassLoader() {
        super(new URL[0]);
    }

    synchronized public void addJar(String jarName) throws MalformedURLException, ClassNotFoundException
    {
        File filePath = new File(jarName);
        URI uriPath = filePath.toURI();
        URL urlPath = uriPath.toURL();

        addURL(urlPath);
    }

    synchronized  public void addJarsByScanDirectory(String dir) throws Exception {
        this.addJarsByScanDirectory(new File(dir));
    }

    synchronized  public void addJarsByScanDirectory(File dir) throws Exception {
        Collection files = FileUtils.listFiles(
                dir,
                new RegexFileFilter(".*\\.jar"),
                DirectoryFileFilter.DIRECTORY
        );

        for(Object file : files) {
            this.addJar(((File) file).getAbsolutePath());
        }
    }

    public static void main(String[] args) throws Exception{
        MultiJarsURLClassLoader multiJarsURLClassLoader = new MultiJarsURLClassLoader();
        multiJarsURLClassLoader.addJarsByScanDirectory("/tmp");
        Thread.currentThread().setContextClassLoader(multiJarsURLClassLoader);
        ServiceLoader<MyPrintService> serviceServiceLoader = ServiceLoader.load(MyPrintService.class);
        Iterator<MyPrintService> iter = serviceServiceLoader.iterator();
        while(iter.hasNext()) {
            iter.next().print();
            // foo
            // bar
        }
    }
}
