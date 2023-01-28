package fr.minemobs.renderingengine.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtils {
    private ThrowableUtils() {}

    public static String toString(Throwable throwable) {
        var sw = new StringWriter();
        try(PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
