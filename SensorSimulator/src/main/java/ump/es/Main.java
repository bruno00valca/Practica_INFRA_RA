package ump.es;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new Factory1());
        Thread t2 = new Thread(new Factory2());
        Thread t3 = new Thread(new Factory3());

        t1.start();
        t2.start();
        t3.start();


    }
}