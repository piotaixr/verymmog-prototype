package com.verymmog;

import com.verymmog.util.MessageQueue;
import com.verymmog.util.Pair;

import java.util.HashSet;
import java.util.Set;

public class TestMessageQueue {

    static class Prod extends Thread {
        MessageQueue<String, String> q;
        int id;
        int i = 0;

        private Prod(int id, MessageQueue<String, String> q) {
            super("Prod " + id);
            this.q = q;
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("La");
                q.add("Prod" + id, "Prod " + id + " " + i++);
                synchronized (this) {
                    try {
                        wait(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static private class Cons extends Thread {
        MessageQueue<String, String> q;
        int id;

        private Cons(int id, MessageQueue<String, String> q) {
            super("Cons " + id);
            this.q = q;
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Pair<String, String> d = q.take();

                    System.out.println(this.getName() + "  :  " + d.first + " --> " + d.second);

                    q.endWork(d.first);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Set<Cons> cons = new HashSet<>();
        Set<Prod> prods = new HashSet<>();
        MessageQueue<String, String> mq = new MessageQueue<>();
        int nbProd = 30;
        int nbCons = 5;

        for (int i = 0; i < nbCons; i++) {
            Cons c = new Cons(i, mq);
            cons.add(c);
            c.start();
        }

        for (int i = 0; i < nbProd; i++) {
            Prod p = new Prod(i, mq);
            prods.add(p);
            p.start();
        }

    }
}
