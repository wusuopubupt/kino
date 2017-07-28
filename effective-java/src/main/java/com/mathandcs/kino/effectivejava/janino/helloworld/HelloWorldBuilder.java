package com.mathandcs.kino.effectivejava.janino.helloworld;

/**
 * Created by dashwang on 7/28/17.
 */
public class HelloWorldBuilder {

    public static String buildHelloWorld() {

        StringBuilder builder = new StringBuilder();
        builder.append("package com.mathandcs.kino.effectivejava.janino.helloworld;");
        builder.append("public class HelloWorldImpl implements HelloWorld {");
        builder.append("    public void printHelloWorld() {");
        builder.append("        System.out.println( \"Hello World!\");");
        builder.append("    }");
        builder.append("}");
        return builder.toString();
    }
}
