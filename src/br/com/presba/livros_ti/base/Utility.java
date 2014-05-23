package br.com.presba.livros_ti.base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import br.com.presba.livros_ti.R;

public class Utility {

	public static final String TAG = "CATHO";
	public static final int OK = 1;
	public static final int CANCEL = 0;

	public static void alertDialog(final Context context, final String message) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage(message);
		dialog.setNeutralButton("OK", null);
		dialog.show();
	}

	public static boolean isNetworkAvailable(Context context,
			boolean messageError) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// VALIDATE CONNECTION
		if (connectivityManager != null) {

			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

			if (info != null) {

				for (int i = 0; i < info.length; i++) {

					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						// OK
						return true;
					}
				}
			}
		}

		// CONNECTION ERROR
		if (messageError) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(R.string.attention);
			dialog.setMessage(R.string.connection_error);
			dialog.setNeutralButton("OK", null);
			dialog.show();
		}

		return false;
	}

	public static final String md5(final String s) {

		try {
			// Cria MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Cria Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			ExceptionManager.setException(e);
		}

		return "";
	}

	public static final int dataMaior(String data1, String data2) {

		try {
			int intData1 = Integer.valueOf(data1.split("/")[2] + ""
					+ data1.split("/")[1] + "" + data1.split("/")[0]);
			int intData2 = Integer.valueOf(data2.split("/")[2] + ""
					+ data2.split("/")[1] + "" + data2.split("/")[0]);

			if (intData1 > intData2) {
				return 1;
			} else if (intData2 > intData1) {
				return 2;
			} else {
				return 0;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}