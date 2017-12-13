package com.mathandcs.kino.effectivejava.compiler.jpython;

import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Created by dashwang on 12/12/17.
 */
public class PythonScriptEvaluator {



    public static void main(String args[]){
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("C:\\Python27\\programs\\my_utils.py");
        PyFunction func = (PyFunction)interpreter.get("adder",PyFunction.class);
        int a = 2010, b = 2 ;
        PyObject pyobj = func.__call__(new PyInteger(a), new PyInteger(b));
        System.out.println("anwser = " + pyobj.toString());
    }
}
