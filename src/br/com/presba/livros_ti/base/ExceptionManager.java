package br.com.presba.livros_ti.base;

import android.util.Log;

public class ExceptionManager {

	public static void setException(Exception e) {
		Log.e(Utility.TAG, e.toString());
		e.printStackTrace();
	}
}
