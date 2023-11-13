package com.dungeonbuilder.utils.java;

public class Trace {
    /**
     * Technically this is the caller caller class name since the caller class name
     * would just be the file this method is being called from!
     * @author https://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java
     * @return
     */
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Trace.class.getName())
                    && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return null;
    }
}
