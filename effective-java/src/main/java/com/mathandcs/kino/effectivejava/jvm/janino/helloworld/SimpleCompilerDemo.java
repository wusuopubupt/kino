package com.mathandcs.kino.effectivejava.jvm.janino.helloworld;

import org.codehaus.janino.SimpleCompiler;

/**
 * Created by dashwang on 7/28/17.
 */
public class SimpleCompilerDemo {

    @SuppressWarnings("all")
    public static void main(String[] args) {
        try {
            // get the text to compile
            String helloWorldClass = HelloWorldBuilder.buildHelloWorld();

            // create the compiler and "cook" the text
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(helloWorldClass);

            // get a reference to the Class
            Class<HelloWorld> clazz = (Class<HelloWorld>) Class.forName(
                    "com.mathandcs.kino.effectivejava.janino.helloworld.HelloWorldImpl", true, compiler
                            .getClassLoader());

            // instantiate and call methods
            HelloWorld instance = clazz.newInstance();
            instance.printHelloWorld();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
