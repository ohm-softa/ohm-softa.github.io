package threads;

public class Hund {
     void laufen(int anzahl) throws InterruptedException {
        int i = 0;
        while (i < anzahl) {
            synchronized (this) {
                wait();
                System.out.println("laufe...");
                Thread.sleep(500);
                i++;
                notify();

            }
            Thread.sleep(10);
        }

    }
    
    void markieren(int anzahl) throws InterruptedException {
        int i = 0;
        while (i < anzahl) {
            synchronized (this) {
                wait();
                System.out.println("markiere...");
                Thread.sleep(150);
                i++;
                notify();
            }
            Thread.sleep(10);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Hund h = new Hund();

        Thread t1 = new Thread(() -> {
            try {
                h.laufen(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                h.markieren(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
