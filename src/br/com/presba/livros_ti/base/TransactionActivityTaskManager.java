package br.com.presba.livros_ti.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class TransactionActivityTaskManager extends AsyncTask<Void, Void, Boolean>{

	private Context context;
	private ITransaction transaction;
	private int messageID;
	
	private Throwable exception;
	private ProgressDialog progress;
	
	public void setTextProgress(String text) {
		progress.setMessage(text);
	}
	
	public TransactionActivityTaskManager(Context context, ITransaction transaction, int messageID){
		this.context = context;
		this.transaction = transaction;
		this.messageID = messageID;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		openProgress();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			this.transaction.execute();
			return true;
		} catch (Exception e) {
			ExceptionManager.setException(e);
			this.exception = e;
			return false;
		} finally {
			closeProgress();
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result) {
			this.transaction.updateView();
		} else {
			Utility.alertDialog(this.context, exception.getMessage());
		}
	}

	private void openProgress() {
		progress = ProgressDialog.show(this.context, "", this.context.getString(this.messageID));
	}
	
	public void closeProgress() {
		if (progress != null)
			progress.dismiss();
	}
}