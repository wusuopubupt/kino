package com.mathandcs.kino.abacus.exp4j;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dashwang on 10/13/17.
 */
public class RuleEngine {

    private static long getMemoryByRule(String rule, Map<String, Double> variableMap) {
        Expression exp = new ExpressionBuilder(rule)
                .variables(variableMap.keySet())
                .build()
                .setVariables(variableMap);
        return (long) exp.evaluate();
    }

    public static void main(String args[]) {

        String rule = "1 + 2 * a * b / c - d";
        Map<String, Double> variableMap = new HashMap<>();
        variableMap.put("a", 1.0);
        variableMap.put("b", 2.0);
        variableMap.put("c", 0.5);
        variableMap.put("e", 4.0);

        assert (5 == getMemoryByRule(rule, variableMap));

    }
}
