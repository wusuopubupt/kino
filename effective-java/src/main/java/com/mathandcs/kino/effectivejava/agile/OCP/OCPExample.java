package com.mathandcs.kino.effectivejava.agile.OCP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 9/16/17.
 */
public class OCPExample {

    static abstract class Shape {
        public abstract void draw();
    }

    static class Square extends Shape {
        @Override
        public void draw() {
            System.out.println("Drawing a square.");
        }
    }

    static class Circle extends Shape {
        @Override
        public void draw() {
            System.out.println("Drawing a circle");
        }
    }

    // good
    private static void drawAllShapes(List<Shape> shapes) {
        for(Shape s : shapes) {
            // 添加新Shape类型, 无需修改已有代码,只需拓展Shape的子类实现draw()方法即可, 遵从了Open for extension原则
            s.draw();
        }
    }

    // bad
    private static void drawAllShapesNonOCP(List<Shape> shapes) {
        for(Shape s: shapes) {
            // 添加新类型,需要修改已有代码,增加新的判断逻辑, 应当避免这种做法, 遵从Closed for modification原则
            if(s instanceof Square) {
                System.out.println("Drawing a square.");
            } else if(s instanceof Circle) {
                System.out.println("Drawing a circle");
            } else {
                System.out.println("Unknown type: " + s.getClass());
            }
        }
    }

    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Square());
        shapes.add(new Square());
        shapes.add(new Circle());

        //drawAllShapesNonOCP(shapes);
        drawAllShapes(shapes);
    }
}
