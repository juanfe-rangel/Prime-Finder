package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Control extends Thread {

    private static final int NTHREADS = 3;
    private static final int MAXVALUE = 30000000;
    private static final int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;
    private PrimeFinderThread[] pft;
    private boolean paused = false;

    private Control() {
        pft = new PrimeFinderThread[NTHREADS];
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            pft[i] = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, this);
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, this);
    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for (PrimeFinderThread t : pft) {
            t.start();
        }

        while (true) {
            try {
                Thread.sleep(TMILISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            pauseAll();
            showPrimes();
            waitForEnter();
            resumeAll();
        }
    }

    public synchronized void pauseAll() {
        paused = true;
    }

    public synchronized void resumeAll() {
        paused = false;
        notifyAll();
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    private int showPrimes() {
        int total = 0;
        for (PrimeFinderThread t : pft) {
            total += t.getPrimeCount();
        }
        System.out.println("Primos encontrados hasta ahora: " + total);
        return total;
    }

    private void waitForEnter() {
        System.out.println("Presione ENTER para continuar");
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
