package com.mathandcs.kino.effectivejava.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.mathandcs.kino.effectivejava.akka.sample.Printer.Greeting;

public class Greeter extends AbstractActor {

  private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  //#greeter-messages
  static public Props props(String message, ActorRef printerActor) {
    return Props.create(Greeter.class, () -> new Greeter(message, printerActor));
  }

  //#greeter-messages
  static public class WhoToGreet {

    public final String who;

    public WhoToGreet(String who) {
      this.who = who;
    }
  }

  static public class Greet {

    public Greet() {
    }
  }
  //#greeter-messages

  private final String message;
  private final ActorRef printerActor;
  private String greeting = "";

  public Greeter(String message, ActorRef printerActor) {
    this.message = message;
    this.printerActor = printerActor;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(WhoToGreet.class, wtg -> {
          this.greeting = message + ", " + wtg.who;
          log.info("Received WhoToGreet: " + this.greeting);
        })
        .match(Greet.class, x -> {
          //#greeter-send-message
          log.info("Telling printActor.");
          printerActor.tell(new Greeting(greeting), getSelf());
          //#greeter-send-message
        })
        .build();
  }
}
