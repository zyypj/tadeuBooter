package com.github.zyypj.tadeuBooter.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtils {
    private static final String LOG_FILE = "plugins/TadeuBooter/logs.txt";

    public static void log(String message) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String formattedMessage = "[" + timestamp + "] " + message;

        System.out.println(formattedMessage);

        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(formattedMessage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}