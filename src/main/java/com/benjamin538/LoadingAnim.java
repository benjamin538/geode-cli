package com.benjamin538;

// for cooldown
import java.time.Duration;

public class LoadingAnim implements Runnable{
    private volatile boolean running = true;
    @Override
    public void run() {
        char block = '█';
        int pos = 1;
        StringBuilder builder = new StringBuilder("[       ]");
        while (running) {
            if (pos == 6) {
                pos = 1;
                builder.setCharAt(6, ' ');
            }
            pos++;
            builder.setCharAt(pos, block);
            builder.setCharAt(pos-1, ' ');
            System.out.print("\r" + builder.toString());
            try{
                Thread.sleep(Duration.ofMillis(500));
            } catch (Exception ex) {
                stop();
                System.err.println(ex.getMessage());
            }
        }
    }

    public void stop() {
        System.out.print("\r");
        running = false;
    }
}
