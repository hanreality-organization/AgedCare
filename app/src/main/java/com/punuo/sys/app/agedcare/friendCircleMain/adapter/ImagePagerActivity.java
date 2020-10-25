package com.punuo.sys.app.agedcare.friendCircleMain.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import java.util.ArrayList;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {
	PopupMenu popup;
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private Button button;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.image_detail_pager);
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);

		mPager = (HackyViewPager) findViewById(R.id.pager);
		button=(Button)findViewById(R.id.image_detail_back);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
//			Window window = getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.setStatusBarColor(getResources().getColor(R.color.contents_text));
//		}

//		mPager.setOnTouchListener(new View.OnTouchListener() {
//			int flage=0;
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch(event.getAction()){
//					case MotionEvent.ACTION_DOWN:
//						flage = 0 ;
//						break ;
//					case MotionEvent.ACTION_MOVE:
//						flage = 1 ;
//						break ;
//					case  MotionEvent.ACTION_UP :
//						if(flage==0){
//							int item=mPager.getCurrentItem();
//							popup = new PopupMenu(ImagePagerActivity.this, v);
//				// 将R.menu.popup_menu菜单资源加载到popup菜单中
//				popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
//				// 为popup菜单的菜单项单击事件绑定事件监听器
//				popup.setOnMenuItemClickListener(
//						new PopupMenu.OnMenuItemClickListener()
//						{
//							@Override
//							public boolean onMenuItemClick(MenuItem item)
//							{
//								switch (item.getItemId())
//								{
//									case R.id.share:
//										//发送图片url分享图片
//										String devId = SipInfo.paddevId;
//										String devName = "pad";
//										final String devType2 = "2";
//										SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//										SipInfo.toDev = new NameAddress(devName, sipURL);
//										org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//												SipInfo.user_from, BodyFactory.createImageShareNotify(EXTRA_IMAGE_URLS));
//										SipInfo.sipUser.sendMessage(query);
//
//										break;
//								}
//								return true;
//							}
//						});
//				popup.show();
//						}
//				}
//				return false;
//			}
//		});
//		mPager.setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				Log.i("11111111","123");
////                showPopMenu(v);
//				popup = new PopupMenu(ImagePagerActivity.this, v);
//				// 将R.menu.popup_menu菜单资源加载到popup菜单中
//				popup.getMenuInflater().inflate(R.menu.popup_menushare, popup.getMenu());
//				// 为popup菜单的菜单项单击事件绑定事件监听器
//				popup.setOnMenuItemClickListener(
//						new PopupMenu.OnMenuItemClickListener()
//						{
//							@Override
//							public boolean onMenuItemClick(MenuItem item)
//							{
//								switch (item.getItemId())
//								{
//									case R.id.share:
//										//发送图片url分享图片
//										String devId = SipInfo.devId;
//										String devName = "pad";
//										final String devType2 = "2";
//										SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//										SipInfo.toDev = new NameAddress(devName, sipURL);
//										org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//												SipInfo.user_from, BodyFactory.createImageShareNotify(EXTRA_IMAGE_URLS));
//										SipInfo.sipUser.sendMessage(query);
//
//										break;
//								}
//								return true;
//							}
//						});
//				popup.show();
//
//				return false;
//			}
//		});
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ArrayList<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}

	}
}
