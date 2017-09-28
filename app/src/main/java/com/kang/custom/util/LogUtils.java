package com.kang.custom.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class LogUtils {

	private static final String TAG = "customLog";
	private static final String LOG_SUFFIX = ".log";
	private static final String LOG_DIR = "CUSTOM_DEBUG";
	private static final String LOG_FILENAME = LOG_DIR + LOG_SUFFIX;

	public static boolean debug = true; // Log switch open, development, released when closed(LogCat)
	private static volatile boolean isWriteFile = false;
	//public static int level = Log.VERBOSE; // Write file level
	public static int level = Log.ERROR; // Write file level

        public enum DateFormater {
            NORMAL("yyyy-MM-dd HH:mm"),
            DD("yyyy-MM-dd"),
            SS_1("yyyy-MM-dd HH:mm:ss"),
            SS_2("yyyy-MM-dd-HH-mm-ss");

            private String value;

            private DateFormater(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

	// -------------------------log.v--------------------------------
	public static void v(String msg) {
		trace(Log.VERBOSE, TAG, msg);
	}

	public static void v(String tag, String msg) {
		trace(Log.VERBOSE, tag, msg);
	}

	public static void v(Throwable tr) {
		trace(Log.VERBOSE, TAG, "", tr);
	}

	public static void v(String msg, Throwable tr) {
		trace(Log.VERBOSE, TAG, msg, tr);
	}

	public static void v(String tag, String msg, Throwable tr) {
		trace(Log.VERBOSE, tag, msg, tr);
	}

	// -------------------------log.d--------------------------------
	public static void d(String msg) {
		trace(Log.DEBUG, TAG, msg);
	}

	public static void d(String tag, String msg) {
		trace(Log.DEBUG, tag, msg);
	}

	public static void d(Throwable tr) {
		trace(Log.DEBUG, TAG, "", tr);
	}

	public static void d(String msg, Throwable tr) {
		trace(Log.DEBUG, TAG, msg, tr);
	}

	public static void d(String tag, String msg, Throwable tr) {
		trace(Log.DEBUG, tag, msg, tr);
	}

	// -------------------------log.i--------------------------------
	public static void i(String msg) {
		trace(Log.INFO, TAG, msg);
	}

	public static void i(String tag, String msg) {
		trace(Log.INFO, tag, msg);
	}

	public static void i(Throwable tr) {
		trace(Log.INFO, TAG, "", tr);
	}

	public static void i(String msg, Throwable tr) {
		trace(Log.INFO, TAG, msg, tr);
	}

	public static void i(String tag, String msg, Throwable tr) {
		trace(Log.INFO, tag, msg, tr);
	}

	// -------------------------log.w--------------------------------
	public static void w(String msg) {
		trace(Log.WARN, TAG, msg);
	}

	public static void w(String tag, String msg) {
		trace(Log.WARN, tag, msg);
	}

	public static void w(Throwable tr) {
		trace(Log.WARN, TAG, "", tr);
	}

	public static void w(String msg, Throwable tr) {
		trace(Log.WARN, TAG, msg, tr);
	}

	public static void w(String tag, String msg, Throwable tr) {
		trace(Log.WARN, tag, msg, tr);
	}

	// -------------------------log.e--------------------------------
	public static void e(String msg) {
		trace(Log.ERROR, TAG, msg);
	}

	public static void e(String tag, String msg) {
		trace(Log.ERROR, tag, msg);
	}

	public static void e(Throwable tr) {
		trace(Log.ERROR, TAG, "", tr);
	}

	public static void e(String msg, Throwable tr) {
		trace(Log.ERROR, TAG, msg, tr);
	}

	public static void e(String tag, String msg, Throwable tr) {
		trace(Log.ERROR, tag, msg, tr);
	}

	/**
	 * Custom Log output style
	 * 
	 * @param type
	 *            Log type
	 * @param tag
	 *            TAG
	 * @param msg
	 *            Log message
	 */
	private static void trace(final int type, String tag, final String msg) {
		if (!debug)
			return;
		
		try {
			// LogCat
			StackTraceElement mStackTraceElement = null;
			String result = "";
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			if (stackTrace != null && stackTrace.length > 4) {
				mStackTraceElement = stackTrace[4];
				result = formatLog(mStackTraceElement);
			}
			StringBuilder sb = new StringBuilder();
			sb.append(result).append(": ").append(msg);
			switch (type) {
			case Log.VERBOSE:
				Log.v(tag, sb.toString());
				break;
			case Log.DEBUG:
				Log.d(tag, sb.toString());
				break;
			case Log.INFO:
				Log.i(tag, sb.toString());
				break;
			case Log.WARN:
				Log.w(tag, sb.toString());
				break;
			case Log.ERROR:
				Log.e(tag, sb.toString());
				break;
			}

			// Write to file
			if (type >= level) {
				if (!TextUtils.isEmpty(msg)) {
					writeLog(type, msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Custom Log output style
	 * 
	 * @param type
	 * @param tag
	 * @param msg
	 * @param tr
	 */
	private static void trace(final int type, final String tag, final String msg, final Throwable tr) {
		// LogCat
		if (!debug)
			return;
		
		try {
			StackTraceElement mStackTraceElement = getStackTraceElement(tr);
			String result = formatLog(mStackTraceElement);
			StringBuilder sb = new StringBuilder();
			sb.append(result).append(": ").append(msg);
			sb.append(getEnterChart()).append(tr == null ? "" : getStackTraceString(tr));
			switch (type) {
			case Log.VERBOSE:
				Log.v(tag, sb.toString());
				break;
			case Log.DEBUG:
				Log.d(tag, sb.toString());
				break;
			case Log.INFO:
				Log.i(tag, sb.toString());
				break;
			case Log.WARN:
				Log.w(tag, sb.toString());
				break;
			case Log.ERROR:
				Log.e(tag, sb.toString());

				break;
			}

			// Write to file
			if (type >= level) {
				String stackTraceString = Log.getStackTraceString(tr);
				if (!TextUtils.isEmpty(stackTraceString)) {
					if(isWriteFile) {
						writeLog(type, (msg == null ? "" : msg) + getEnterChart()
								+ (stackTraceString == null ? "" : stackTraceString));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getEnterChart() {
		return "\r\n";
	}

	public static StackTraceElement getStackTraceElement(Throwable tr) {
		StackTraceElement mStackTraceElement = null;
		if (tr != null) {
			StackTraceElement[] stacktraceTr = tr.getStackTrace();
			if (stacktraceTr != null && stacktraceTr.length > 0)
				mStackTraceElement = stacktraceTr[0];
		}
		return mStackTraceElement;
	}

	/**
	 * @param mStackTraceElement
	 * @return className[methodName, lineNumber]
	 */
	public static String formatLog(StackTraceElement mStackTraceElement) {
		String result = "";
		if (mStackTraceElement != null) {
			String tag = "%s[%s, %d]";
			String callerClazzName = mStackTraceElement.getClassName();
			if (!TextUtils.isEmpty(callerClazzName) && callerClazzName.contains(".") && !callerClazzName.endsWith(".")) {
				try {
					callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = String.format(tag, callerClazzName, mStackTraceElement.getMethodName(),
					mStackTraceElement.getLineNumber());
		}
		return result;
	}

	/**
	 * Write log file to the SDCard
	 * 
	 * @param type
	 * @param msg
	 */
	private static void writeLog(int type, String msg) {
		if (!isHasSDCard()) {
			return;
		}

		if (TextUtils.isEmpty(msg))
			return;

		try {
			SparseArray<String> logMap = new SparseArray<String>();
			logMap.put(Log.VERBOSE, " VERBOSE ");
			logMap.put(Log.DEBUG, " DEBUG ");
			logMap.put(Log.INFO, " INFO ");
			logMap.put(Log.WARN, " WARN ");
			logMap.put(Log.ERROR, " ERROR ");

			StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
			StackTraceElement mStackTraceElement = null;
			if (stackTrace != null && stackTrace.length > 3) {
				mStackTraceElement = stackTrace[3];
			}

			StringBuilder sb = new StringBuilder().append(getEnterChart()).append(
					getDateFormat(DateFormater.SS_1.getValue()));
			if (mStackTraceElement != null) {
				sb.append(logMap.get(type)).append(mStackTraceElement.getClassName()).append(" - ")
						.append(mStackTraceElement.getMethodName()).append("()");
			}
			sb.append(": ").append(msg);

			if (!TextUtils.isEmpty(LOG_DIR))
				recordLog(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + LOG_DIR,
						LOG_FILENAME, sb.toString(), true);
		} catch (Exception e) {
			LogUtils.e(e.getMessage());
		}
	}

	/**
	 * Write log
	 * 
	 * @param logDir
	 *            Log path to save
	 * @param fileName
	 * @param msg
	 *            Log content
	 * @param append
	 *            Save as type, false override save, true before file add save
	 */
	private static void recordLog(String logDir, String fileName, String msg, boolean append) {
		try {
			createDir(logDir);

			final File saveFile = new File(new StringBuffer().append(logDir).append(File.separator).append(fileName)
					.toString());

			if (!append && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				write(saveFile, msg, append);
			} else if (append && saveFile.exists()) {
				long fileLen = saveFile.length();
				if (fileLen > 1 * 1024 * 1024) {//1M
					saveFile.delete();
					saveFile.createNewFile();
				}
				write(saveFile, msg, append);
			} else if (append && !saveFile.exists()) {
				saveFile.createNewFile();
				write(saveFile, msg, append);
			} else if (!append && !saveFile.exists()) {
				saveFile.createNewFile();
				write(saveFile, msg, append);
			}
		} catch (IOException e) {
			e.printStackTrace();
			recordLog(logDir, fileName, msg, append);
		}
	}

	private static String getDateFormat(String pattern) {
		final DateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}

	private static File createDir(String dir) {
		final File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * Write msg to file
	 * 
	 * @param file
	 * @param msg
	 * @param append
	 */
	private static void write(final File file, final String msg, final boolean append) {
		if (TextUtils.isEmpty(msg))
			return;

		final FileOutputStream fos;
		try {
			fos = new FileOutputStream(file, append);
			try {
				fos.write(msg.getBytes());
			} catch (IOException e) {
				LogUtils.e(TAG, "write fail!!!", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						LogUtils.d(TAG, "Exception closing stream: ", e);
					}
				}
			}
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, "write fail!!!", e);
		}
	}

	public static boolean isHasSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

	// log4j配置
	public static void configLog4j() {
		/*
		 * LogConfigurator lc = new LogConfigurator(); lc.setFileName(Environment.getExternalStorageDirectory() +
		 * File.separator + LOG_DIR + File.separator + "log4j.txt"); lc.setRootLevel(LogLevel.DEBUG);
		 * lc.setLevel("org.apache", LogLevel.ERROR); lc.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		 * lc.setMaxFileSize(2 * 1024 * 1024); lc.setImmediateFlush(true); lc.configure();
		 */
	}

	public static void setIsWriteFile(boolean isWriteFile) {
		LogUtils.isWriteFile = isWriteFile;
	}
}
