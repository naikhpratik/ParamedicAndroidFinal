package com.code3apps.para.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
	public static final boolean DEBUG = true;

	/** 系统默认的UncaughtException处理类 */

	private Thread.UncaughtExceptionHandler mDefaultHandler;

	/** CrashHandler实例 */

	private static CrashHandler INSTANCE;

	/** 程序的Context对象 */

	// private Context mContext;

	/** 保证只有一个CrashHandler实例 */

	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */

	public static CrashHandler getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */

	public void init(Context ctx) {
		// mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		saveCrashInfo2File(ex);
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}

	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @return true代表处理该异常，不再向上抛异常，
	 *         false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
	 *         简单来说就是true不会弹出那个错误提示框，false就会弹出
	 */

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}

		// final String msg = ex.getLocalizedMessage();
		final StackTraceElement[] stack = ex.getStackTrace();
		final String message = ex.getMessage();

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {

				Looper.prepare();
//				 Toast.makeText(mContext, "程序出错啦:" + message,
//				 Toast.LENGTH_LONG).show();
				// 可以只创建一个文件，以后全部往里面append然后发送，这样就会有重复的信息，个人不推荐

				String fileName = "crash-" + System.currentTimeMillis() + ".log";
				File file = new File(Environment.getExternalStorageDirectory(), fileName);
				Log.i("k", file.getAbsolutePath());
				try {
					FileOutputStream fos = new FileOutputStream(file, true);
					fos.write(message.getBytes());
					for (int i = 0; i < stack.length; i++) {
						fos.write(stack[i].toString().getBytes());
					}

					fos.flush();
					fos.close();
				} catch (Exception e) {
				}
				Looper.loop();
			}

		}.start();
		return false;
	}

	// TODO 使用HTTP Post 发送错误报告到服务器 这里不再做详细描述
	// private void postReport(File file) {

	// }

	
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private Map<String, String> infos = new HashMap<String, String>();
	/** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return  返回文件名称,便于将文件传送到服务器 
     */  
    private String saveCrashInfo2File(Throwable ex) {  
          
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".log";  
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = Environment.getExternalStorageDirectory() + "/crash/";  
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
            return fileName;  
        } catch (Exception e) {  
            Log.i("asd", "an error occured while writing file...", e);  
        }  
        return null;  
    }
}
