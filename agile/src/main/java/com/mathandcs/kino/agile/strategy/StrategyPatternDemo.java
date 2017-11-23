package com.mathandcs.kino.agile.strategy;

public class StrategyPatternDemo {
   public static void main(String[] args) {
      Context context = new Context(new AddStrategy());
      System.out.println("10 + 5 = " + context.calculate(10, 5));

      context = new Context(new SubtractStrategy());
      System.out.println("10 - 5 = " + context.calculate(10, 5));
   }
}
