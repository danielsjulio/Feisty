package com.feisty.utils;

import android.util.Log;

import com.feisty.BuildConfig;

/**
 * Created by Gil on 11/06/15.
 */
public class Logger {

    public final String tag;

    /** Creates a {@link Logger} with the calling class's class name as a tag. */
    public static final Logger create() {
        return new Logger(getTag());
    }

    /** Creates a {@link Logger} with a manually-specified tag. */
    public static final Logger create(String tag) {
        return new Logger(tag);
    }

    private Logger(String tag) {
        this.tag = tag;
    }

    public void v(String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, formatIfNeeded(message, args));
        }
    }

    public void v(Throwable t, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, formatIfNeeded(message, args), t);
        }
    }

    public void d(String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, formatIfNeeded(message, args));
        }
    }

    public void d(Throwable t, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, formatIfNeeded(message, args), t);
        }
    }

    public void i(String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, formatIfNeeded(message, args));
        }
    }

    public void i(Throwable t, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, formatIfNeeded(message, args), t);
        }
    }

    public void w(String message, Object... args) {
        Log.w(tag, formatIfNeeded(message, args));
    }

    public void w(Throwable t, String message, Object... args) {
        Log.w(tag, formatIfNeeded(message, args), t);
    }

    public void e(String message, Object... args) {
        Log.e(tag, formatIfNeeded(message, args));
    }

    public void e(Throwable t, String message, Object... args) {
        Log.e(tag, formatIfNeeded(message, args), t);
    }

    private static final String getTag() {
        String[] parts = new Throwable().getStackTrace()[2].getClassName().split("\\.");
        return "primrose/" + parts[parts.length - 1];
    }

    private static String formatIfNeeded(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        } else {
            return String.format(message, args);
        }
    }
}