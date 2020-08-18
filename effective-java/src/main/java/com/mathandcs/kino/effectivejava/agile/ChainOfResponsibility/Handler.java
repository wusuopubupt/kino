package com.mathandcs.kino.effectivejava.agile.ChainOfResponsibility;

/**
 * Created by dashwang on 11/21/17.
 *
 * 首先定义一个抽象的处理角色Handler ,其次是具体实现类ConcreteHandler,
 * 在ConcreteHandler 我们通过getNextHandler()来判断是否还有下一个责任链, 如果有,则急需传递下去,
 * 调用getNextHandler().handle()来实现。
 *
 */
public abstract class Handler {

    private Handler nextHandler;

    public Handler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * @param user : 报销申请人
     * @param fee: 报销费用
     */
    public abstract void handle(String user, double fee);

}
