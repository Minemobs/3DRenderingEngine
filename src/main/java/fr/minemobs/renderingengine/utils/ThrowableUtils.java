package fr.minemobs.renderingengine.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtils {
    private ThrowableUtils() {}

    public static String toString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
