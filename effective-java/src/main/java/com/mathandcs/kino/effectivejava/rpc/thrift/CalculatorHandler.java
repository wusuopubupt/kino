package com.mathandcs.kino.effectivejava.rpc.thrift;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.mathandcs.kino.effectivejava.rpc.thrift.shared.SharedStruct;
import com.mathandcs.kino.effectivejava.rpc.thrift.tutorial.Calculator;
import com.mathandcs.kino.effectivejava.rpc.thrift.tutorial.InvalidOperation;
import com.mathandcs.kino.effectivejava.rpc.thrift.tutorial.Work;

import java.util.HashMap;

// Generated code

public class CalculatorHandler implements Calculator.Iface {

    private HashMap<Integer, SharedStruct> log;

    public CalculatorHandler() {
        log = new HashMap<Integer, SharedStruct>();
    }

    @Override
    public void ping() {
        System.out.println("ping()");
    }

    @Override
    public int add(int n1, int n2) {
        System.out.println("add(" + n1 + "," + n2 + ")");
        return n1 + n2;
    }

    @Override
    public int calculate(int logid, Work work) throws InvalidOperation {
        System.out.println("calculate(" + logid + ", {" + work.op + "," + work.num1 + "," + work.num2 + "})");
        int val = 0;
        switch (work.op) {
            case ADD:
                val = work.num1 + work.num2;
                break;
            case SUBTRACT:
                val = work.num1 - work.num2;
                break;
            case MULTIPLY:
                val = work.num1 * work.num2;
                break;
            case DIVIDE:
                if (work.num2 == 0) {
                    InvalidOperation io = new InvalidOperation();
                    io.whatOp = work.op.getValue();
                    io.why = "Cannot divide by 0";
                    throw io;
                }
                val = work.num1 / work.num2;
                break;
            default:
                InvalidOperation io = new InvalidOperation();
                io.whatOp = work.op.getValue();
                io.why = "Unknown operation";
                throw io;
        }

        SharedStruct entry = new SharedStruct();
        entry.key = logid;
        entry.value = Integer.toString(val);
        log.put(logid, entry);

        return val;
    }

    @Override
    public SharedStruct getStruct(int key) {
        System.out.println("getStruct(" + key + ")");
        return log.get(key);
    }

    @Override
    public void zip() {
        System.out.println("zip()");
    }

}
