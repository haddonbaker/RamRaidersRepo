package edu.gcc.ramraiders;

import java.time.LocalDateTime;

public class Log {

    public static final int LEVEL_ERROR = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_DEBUG = 3;

    private static final String beginRedColor = "\\x1b[31m";
    private static final String endColor = "\\x1b[0m";
    private static final String beginGreenColor = "\\x1b[32m";
    private static final String beginGrayColor = "\\x1b[37m";

    java.io.PrintStream out;
    int level = LEVEL_INFO;

    public Log(java.io.PrintStream out) {
        this.out = out;
    }

    public String getLevelString() {
        if (level == LEVEL_ERROR) return "ERROR";
        else if (level == LEVEL_INFO) return "INFO";
        else if (level == LEVEL_WARN) return "WARN";
        else if (level == LEVEL_DEBUG) return "DEBUG";
        else return "INFO";
    }

    public void error(String message) {
        if (level >= LEVEL_ERROR) {
            out.println(LocalDateTime.now() + beginRedColor + message + endColor);
        }
    }

    public void info(String message) {
        if (level >= LEVEL_INFO) {
            out.println(LocalDateTime.now() + beginGreenColor + message + endColor);
        }
    }
    public void debug(String message) {
        if (level >= LEVEL_DEBUG) {
            out.println(LocalDateTime.now() + beginGrayColor + message + endColor);
        }
    }
}
