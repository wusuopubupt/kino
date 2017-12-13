package com.mathandcs.kino.effectivejava.jvm.jython;

import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import java.nio.file.Paths;

/**
 * Created by dashwang on 12/12/17.
 */
public class PythonScriptEvaluator {

    private static PythonInterpreter interpreter = new PythonInterpreter();

    private static final String PYTHON_UTIL_SCRIPT_PATH = "effective-java/src/main/resources/python/my_util.py";
    private static final String PYTHON_MAIN_SCRIPT_PATH = "effective-java/src/main/resources/python/main.py";
    private static final String PYTHON_UTIL_SCRIPT_RELATIVE_CLASSPATH = "effective-java/src/main/resources/python";

    private static void callPythonFunction() {
        PyFunction func = (PyFunction)interpreter.get("add",PyFunction.class);
        int a = 1, b = 2 ;
        PyObject pyobj = func.__call__(new PyInteger(a), new PyInteger(b));
        System.out.println("answer = " + (long) pyobj.__tojava__(Long.class));
    }

    // http://blog.csdn.net/xfei365/article/details/50996727, solved jython "ImportError: No module named" error
    private static void reloadClassPath() {
        String curDir = Paths.get(".").toAbsolutePath().normalize().toString();
        interpreter.exec("import sys");
        interpreter.exec(String.format("sys.path.append('%s')", Paths.get(curDir, PYTHON_UTIL_SCRIPT_RELATIVE_CLASSPATH)));
        interpreter.exec("print(sys.path)");
    }

    public static void main(String args[]){
        reloadClassPath();

        // call python function
        interpreter.execfile(PYTHON_UTIL_SCRIPT_PATH);
        callPythonFunction();

        // execute python file directly
        interpreter.execfile(PYTHON_MAIN_SCRIPT_PATH);
    }
}
