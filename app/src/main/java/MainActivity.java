package com.packag;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	private WebView webview1;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		webview1 = findViewById(R.id.webview1);
		webview1.getSettings().setJavaScriptEnabled(true);
		webview1.getSettings().setSupportZoom(true);
		
		webview1.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
				final String _url = _param2;
				
				super.onPageStarted(_param1, _param2, _param3);
			}
			
			@Override
			public void onPageFinished(WebView _param1, String _param2) {
				final String _url = _param2;
				
				super.onPageFinished(_param1, _param2);
			}
		});
	}
	
	private void initializeLogic() {
		android.webkit.WebView my_web = (android.webkit.WebView) findViewById(R.id.webview1);
		
		android.webkit.WebSettings webSettings = my_web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		
		my_web.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);
		
		class BolAIInterface {
			    android.content.Context context;
			    BolAIInterface(android.content.Context c) { context = c; }
			    
			    @android.webkit.JavascriptInterface
			    public void copyToClipboard(final String text) {
				        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				        android.content.ClipData clip = android.content.ClipData.newPlainText("BolAI", text);
				        clipboard.setPrimaryClip(clip);
				        
				        // Lambda () -> ko hata kar new Runnable() use kiya gaya hai
				        ((android.app.Activity)context).runOnUiThread(new Runnable() {
					            @Override
					            public void run() {
						                SketchwareUtil.showMessage(context, "Copied to Clipboard!");
						            }
					        });
				    }
		}
		my_web.addJavascriptInterface(new BolAIInterface(this), "JSInterface");
		
		my_web.setDownloadListener(new android.webkit.DownloadListener() {
			    @Override
			    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				        try {
					            if (url.startsWith("http")) {
						                android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
						                
						                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
						                request.addRequestHeader("cookie", cookies);
						                request.addRequestHeader("User-Agent", userAgent);
						                
						                // Get Filename
						                String fileName = android.webkit.URLUtil.guessFileName(url, contentDisposition, mimetype);
						                request.setTitle(fileName);
						                request.setDescription("Downloading file 👍...");
						                
						                // Visibility & Storage
						                request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						                request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName);
						                
						                android.app.DownloadManager dm = (android.app.DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
						                dm.enqueue(request);
						                
						                SketchwareUtil.showMessage(getApplicationContext(), "Download Started: " + fileName);
						            } else {
						                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
						                intent.setData(android.net.Uri.parse(url));
						                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK); 
						                startActivity(intent);
						            }
					        } catch (Exception e) {
					            SketchwareUtil.showMessage(getApplicationContext(), "Error: please goto https://bol-ai.vercel.app for download");
					        }
				    }
		});
		
		my_web.setWebChromeClient(new android.webkit.WebChromeClient());
		my_web.setWebViewClient(new android.webkit.WebViewClient());
		webview1.loadUrl("file:///android_asset/index.html");
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}

