package com.benjamin538;

import java.time.Duration;

public class LoadingAnim implements Runnable{
    public boolean running = true;
    @Override
    public void run() {
        char block = '█';
        int pos = 2;
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
                System.err.println(ex.getMessage());
             }
        }
    }
}
