package client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class ServiceMessagesHistory {
    private PrintWriter printWriter;
    private String fileName;
    private String login;

    public void open(String login) {
        this.login = login;
        this.fileName = "history_[" + login + "].txt";
        try {
            printWriter = new PrintWriter(new FileOutputStream(fileName, true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }

    public void writeLine(String line) {
        if (printWriter != null) {
            printWriter.println(line);
        }
    }

    public String getLastMessage() {

        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder sB = new StringBuilder();
        System.out.println(lines.size());

        for (int i = 0; i < Math.min(lines.size(), 100); i++) {
            sB.append(lines.get(i)).append("\n");
        }

        sB.append("---История успешно загружена---").append("\n");

        return sB.toString();
    }

}
