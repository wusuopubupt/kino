package com.mathandcs.kino.effectivejava.akka.sample;

import com.mathandcs.kino.effectivejava.akka.sample.Greeter.Greet;
import com.mathandcs.kino.effectivejava.akka.sample.Greeter.WhoToGreet;
import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * https://developer.lightbend.com/guides/akka-quickstart-java/
 */
public class AkkaQuickStart {
  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("helloakka");
    try {
      //#create-actors
      final ActorRef printerActor =
          system.actorOf(Printer.props(), "printerActor");
      final ActorRef howdyGreeter =
          system.actorOf(Greeter.props("Howdy", printerActor), "howdyGreeter");
      final ActorRef helloGreeter =
          system.actorOf(Greeter.props("Hello", printerActor), "helloGreeter");
      final ActorRef goodDayGreeter =
          system.actorOf(Greeter.props("Good day", printerActor), "goodDayGreeter");
      //#create-actors

      //#main-send-messages
      howdyGreeter.tell(new WhoToGreet("Akka"), ActorRef.noSender());
      howdyGreeter.tell(new Greet(), ActorRef.noSender());

      howdyGreeter.tell(new WhoToGreet("Lightbend"), ActorRef.noSender());
      howdyGreeter.tell(new Greet(), ActorRef.noSender());

      helloGreeter.tell(new WhoToGreet("Java"), ActorRef.noSender());
      helloGreeter.tell(new Greet(), ActorRef.noSender());

      goodDayGreeter.tell(new WhoToGreet("Play"), ActorRef.noSender());
      goodDayGreeter.tell(new Greet(), ActorRef.noSender());
      //#main-send-messages

      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ioe) {
    } finally {
      system.terminate();
    }
  }
}