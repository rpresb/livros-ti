package br.com.presba.livros_ti.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.com.presba.livros_ti.R;

public class ActivityBase extends Activity {

	private TransactionActivityTaskManager transactionTaskManager;

	protected ImageButton btnBack;
	protected ImageButton btnMenu;
	protected ImageButton btnOptions;
	protected TextView txvTitle;
	protected LinearLayout layMenu;
	protected LinearLayout layConteudo;
	protected ListView lstMenu;

	protected boolean isExpanded;
	protected int screenWidth;

	private static final ScheduledExecutorService worker = Executors
			.newSingleThreadScheduledExecutor();

	//public static MDLLogin AuthenticatedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
	}

	public void startTransaction(ITransaction transaction, int messageID) {

		transactionTaskManager = new TransactionActivityTaskManager(this,
				transaction, messageID);
		transactionTaskManager.execute();
	}

	public void setTextProgress(int textID) {
		transactionTaskManager.setTextProgress(getString(textID));
	}

	protected void showMessage(final String title, final String message,
			final boolean finishActivity) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (finishActivity)
					finish();
			}
		});
		dialog.show();
	}

	protected void loadMenu(int id) {
		loadMenu(id, false);
	}

	protected void loadMenu(int id, boolean showBack) {
//
//		CTLMenu menu = new CTLMenu();
//		ADPMenu adpMenu = new ADPMenu(menu.getMenuList(), this, id);
//
//		btnMenu = (ImageButton) findViewById(R.id.btnMenu);
//		btnBack = (ImageButton) findViewById(R.id.btnBack);
//		layConteudo = (LinearLayout) findViewById(R.id.layConteudo);
//		layMenu = (LinearLayout) findViewById(R.id.layMenu);
//		lstMenu = (ListView) findViewById(R.id.lstMenu);
//
//		btnMenu.setVisibility(View.GONE);
//		btnBack.setVisibility(View.GONE);
//		if (!showBack) {
//			btnMenu.setVisibility(View.VISIBLE);
//		} else {
//			btnBack.setVisibility(View.VISIBLE);
//		}
//
//		layConteudo.setMinimumWidth(screenWidth);
//		LayoutParams lp = layConteudo.getLayoutParams();
//		lp.width = screenWidth;
//		layConteudo.setLayoutParams(lp);
//
//		lstMenu.setAdapter(adpMenu);
//
//		btnMenu.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				if (isExpanded) {
//					isExpanded = false;
//					layMenu.startAnimation(new ANIMCollapseSliderMenuList(
//							layMenu));
//				} else {
//					isExpanded = true;
//					layMenu.startAnimation(new ANIMExpandSliderMenuList(
//							layMenu, (int) (screenWidth - btnMenu.getWidth())));
//				}
//			}
//		});
//
//		btnBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ActivityBase.this.finish();
//			}
//		});
//
//		lstMenu.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view,
//					int position, long id) {
//
//				MDLMenu menuItem = (MDLMenu) adapterView.getAdapter().getItem(
//						position);
//
//				switch (menuItem.getId()) {
//				case CTLMenu.ITEM_HOME:
//					startActivity(new Intent(ActivityBase.this, Main.class));
//					break;
//				case CTLMenu.ITEM_MESSAGE_INBOX:
//					startActivity(new Intent(ActivityBase.this,
//							MessageInbox.class));
//					break;
//				case CTLMenu.ITEM_JOBS_NEW_SEARCH:
//					startActivity(new Intent(ActivityBase.this, SearchJob.class));
//					break;
//
//				case CTLMenu.ITEM_ABOUT_PRIVACY:
//
//					startActivity(new Intent(ActivityBase.this,PoliticaPrivacidade.class));
//
//					break;
//				case CTLMenu.ITEM_NEWS_CAREER:
//						startActivity(new Intent(ActivityBase.this,DicasECarreiras.class));
//
//					break;
//				case CTLMenu.ITEM_MESSAGE_SCHEDULE:
//					startActivity(new Intent(ActivityBase.this,ListaEntrevistas.class));
//				break;
//				case CTLMenu.ITEM_ABOUT_APP:
//					startActivity(new Intent(ActivityBase.this,SobreApp.class));
//				break;
//
//
//
//				case CTLMenu.ITEM_EXIT:
//					// Store to preferences
//					SharedPreferences settings = PreferenceManager
//							.getDefaultSharedPreferences(getBaseContext());
//					Editor editor = settings.edit();
//					editor.remove("user");
//					editor.remove("password");
//					editor.commit();
//
//					Process.killProcess(Process.myPid());
//					return;
//				}
//
//				isExpanded = false;
//				layMenu.startAnimation(new ANIMCollapseSliderMenuList(layMenu));
//
//				Runnable task = new Runnable() {
//					public void run() {
//						ActivityBase.this.finish();
//					}
//				};
//				worker.schedule(task, 1, TimeUnit.SECONDS);
//			}
//		});
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (transactionTaskManager != null) {
			boolean execute = transactionTaskManager.getStatus().equals(
					AsyncTask.Status.RUNNING);
			if (execute) {
				transactionTaskManager.cancel(true);
				transactionTaskManager.closeProgress();
			}
		}
	}

}
