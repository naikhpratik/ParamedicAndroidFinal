package com.code3apps.para.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import com.code3apps.para.beans.ExplanationSettingBean;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Util {

	private static final String TAG = "Util";
	private static final boolean DEBUG_ENABLED = false;
	private static final String SHAREPRE = "emt_share";
	public static final int DEFAULT_VALUE = 888;
	/**
	 * 
	 * @param arr_int
	 * @return
	 */
	public static int[] getArrRandomSequence(int[] arr_int) {
		Random random = new Random();
		int no = 0;
		if (arr_int != null && arr_int.length > 0) {
			no = arr_int.length;
			for (int i = 0; i < no; i++) {
				int p = random.nextInt(no);
				int tmp = arr_int[i];
				arr_int[i] = arr_int[p];
				arr_int[p] = tmp;
			}
			log("length of arr_int is " + no);
		}
		random = null;
		return arr_int;
	}

	/**
	 * Read file by row
	 * 
	 * @param fileName
	 *            : path and name of a file
	 * @return
	 * 
	 */
	public static StringBuilder readFileByLines(InputStream file_inputstream) {

		// File file = new File(fileName);
		StringBuilder strB = new StringBuilder();
		String tempString = "";

		InputStreamReader inputReader = new InputStreamReader(file_inputstream);
		BufferedReader bufReader = new BufferedReader(inputReader);

		try {

			// read one row once until it's ended
			while ((tempString = bufReader.readLine()) != null) {
				// log(tempString);
				strB.append(tempString);
				strB.append("\n");
			}
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e1) {
				}
			}
		}
		return strB;
	}
	
	public static String trimString(String strSource){
		if(strSource!=null){
			strSource = strSource.trim();
		}
		return strSource;
	}

	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		File f = new File(path);
		if(f.exists()){
			f.delete();
			return true;
		}
		return false;
	}
	public static String getExplanation(String strHtmlTemplate,
			ExplanationSettingBean eSetting) {
		String strHtmlTemp = strHtmlTemplate;
		if (strHtmlTemp != null && strHtmlTemp.length() > 0) {
			strHtmlTemp = strHtmlTemp.replace("##explanation_content##", eSetting
					.getExplanation_content());
			strHtmlTemp = strHtmlTemp.replace("##font_size##", eSetting.getFont_size());
		}
		return strHtmlTemp;
	}

	public static boolean isConnectInternet(Activity activity) {
		ConnectivityManager conManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isConnectInternet(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	
	public static int getFirstCompleteFlag(Context context) {
		SharedPreferences shared = context.getSharedPreferences(SHAREPRE,
				Context.MODE_PRIVATE);
		return shared.getInt("first", DEFAULT_VALUE);
	}
	public static void putFirstCompleteFlag(Context context, int value){
		SharedPreferences shared_pre = context.getSharedPreferences(SHAREPRE, Context.MODE_PRIVATE);
		Editor edit = shared_pre.edit();
		edit.putInt("first", value);
		edit.commit();
	}

	public static int getStartAppTimes(Context context) {
		SharedPreferences shared = context.getSharedPreferences(SHAREPRE,
				Context.MODE_PRIVATE);
		return shared.getInt("times", 0);
	}
	public static void putStartAppTimes(Context context, int value){
		SharedPreferences setting = context.getSharedPreferences(SHAREPRE, Context.MODE_PRIVATE);
		Editor edit = setting.edit();
		edit.putInt("times", value);
		edit.commit();
	}
	public static void putTwitterAccessToken(Context context, String value) {
		SharedPreferences setting = context.getSharedPreferences(SHAREPRE, Context.MODE_PRIVATE);
		Editor edit = setting.edit();
		edit.putString("accesstoken", value);
		edit.commit();
	}

	public static void putTwitterTokenSectet(Context context, String value) {
		SharedPreferences setting = context.getSharedPreferences(SHAREPRE, Context.MODE_PRIVATE);
		Editor edit = setting.edit();
		edit.putString("tokensecret", value);
		edit.commit();
	}
	
	public static String getTwitterAccessToken(Context context) {
		SharedPreferences shared = context.getSharedPreferences(SHAREPRE,
				Context.MODE_PRIVATE);
		return shared.getString("accesstoken", null);
	}
	
	public static String getTwitterTokenSectet(Context context) {
		SharedPreferences shared = context.getSharedPreferences(SHAREPRE,
				Context.MODE_PRIVATE);
		return shared.getString("tokensecret", null);
	}
	
	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

}
