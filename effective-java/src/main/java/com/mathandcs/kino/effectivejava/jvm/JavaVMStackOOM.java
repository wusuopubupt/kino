package com.mathandcs.kino.effectivejava.jvm;

/**
 * @author dashwang
 */
public class JavaVMStackOOM {

    private void dontStop(int i) {
        System.out.print("Thread index: " + i + "\n");
        while (true) {

        }
    }

    public void stackLeafByThread() throws Exception{
        int i = 0;
        while (true) {
            final int currentIdx = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dontStop(currentIdx);
                }
            });
            thread.start();
            i++;
        }
    }

    public static void main(String[] args) throws Exception{
        JavaVMStackOOM oom = new JavaVMStackOOM();
        oom.stackLeafByThread();
    }

}
