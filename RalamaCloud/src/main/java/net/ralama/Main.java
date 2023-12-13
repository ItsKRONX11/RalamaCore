package net.ralama;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {
    protected static final Logger logger = LogManager.getLogger("Ralama");

    public static void main(String... args) {
        System.out.println("Starting RalamaCloud by ItsKRONX11...");
        System.out.println("Version: 1.9");

        ColoredConsole.printStream = System.out;
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String x) {
                logger.info(x);
            }
            @Override
            public void println(int i) {
                logger.info(Integer.toString(i));
            }
        });

        new RalamaCloud(logger).start();

        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            logger.info("Command line input ready");
            while (true) {
                try {
                    Ralama.getCommandManager().dispatchCommand(Ralama.getPlayer("CloudAdmin"), reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}