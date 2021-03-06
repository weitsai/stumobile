package edu.stu.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;
import edu.stu.mobile.ui.ViewPagerAdapter;
import edu.stu.mobile.util.ResolutionUtils;

public class Main extends Activity implements OnPageChangeListener {

	private TextView about;
	private ViewPager functionMenu;
	private ViewFlipper panel;
	private LinearLayout pageNum;
	private List<View> functionPage;
	private String PanelUrls[] = { "http://freshman.stu.edu.tw/", "http://ccds2012.stu.edu.tw/" };
	private final String TAG = "Main";
	private int CurrentPag = 0;

	private void initPanel() {
		int PanelImages[] = { R.drawable.a1, R.drawable.a2 };
		// 動畫輪播更換頻率 (ms)
		int rateFlash = 5000;
		panel.setInAnimation(this, R.anim.in_leftright);
		panel.setOutAnimation(this, R.anim.out_leftright);
		panel.setFlipInterval(rateFlash);
		panel.setAutoStart(true);
		panel.setOnTouchListener(new OnTouchListener() {
			private boolean isMoveing = false;
			private double x, y;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					this.x = event.getX();
					this.y = event.getY();
					break;

				case MotionEvent.ACTION_UP:
					double Judgment = x - event.getX();
					if (Math.abs(Judgment) < 50 && Math.abs(this.y - event.getY()) < 50) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PanelUrls[panel.getDisplayedChild()])));
						break;
					}

					if (!isMoveing) {
						if (Judgment > 150) {
							panel.setInAnimation(Main.this, R.anim.in_rightleft);
							panel.setOutAnimation(Main.this, R.anim.out_rightleft);
							panel.showNext();
						} else {
							panel.setInAnimation(Main.this, R.anim.in_leftright);
							panel.setOutAnimation(Main.this, R.anim.out_leftright);
							panel.showPrevious();
						}
					}

					panel.getInAnimation().setAnimationListener(new AnimationListener() {

						public void onAnimationEnd(Animation arg0) {
							panel.setAutoStart(true);
							isMoveing = false;
							panel.stopFlipping();
						}

						public void onAnimationRepeat(Animation arg0) {

						}

						public void onAnimationStart(Animation arg0) {
							panel.setAutoStart(false);
							isMoveing = true;
						}

					});

					break;

				}

				return true;
			}
		});

		setPanelData(PanelImages);
	}

	private void setPanelData(int[] PaneImages) {
		if (PaneImages != null) {
			for (int index = 0; index < PaneImages.length; index++) {
				panel.addView(newPanelView(PaneImages[index]));
			}
		}
	}

	private View newPanelView(int PaneImage) {
		View v = new View(this);
		// -2 is wrap_cotent
		v.setLayoutParams(new LayoutParams(-2, -2));
		v.setBackgroundResource(PaneImage);
		return v;
	}

	/**
	 * 初始話所需要使用的 icon 及 title，如需要請在此增加（ 兩者數量需一致 ）
	 */
	private void initFunctionMenuitem() {
		// 各功能Icon
		int FunctionMenuIcon[] = { R.drawable.ext_table, };

		// 個功能名稱 （建議使用 Strings.xml 新增，以便多國語系使用）
		String FunctionMenuText[] = { getString(R.string.ext_table), };

		Class Purpose[] = { Main.class };

		setFunctionMenuData(FunctionMenuIcon, FunctionMenuText, Purpose);
	}

	/**
	 * 新增下方頁面頁碼
	 * 
	 * @param number
	 *            此點為第幾頁
	 * @param color
	 *            使用 Color.BLUE 即可取得藍點，若使用 Color.BLACK 即會得到黑點
	 */
	private void addFunctionMenuPagDot(int number, int color) {
		View dot = new View(this);

		switch (color) {
		case Color.BLUE:
			dot.setBackgroundResource(R.drawable.dot_blue);
			break;
		case Color.BLACK:
			dot.setBackgroundResource(R.drawable.dot_black);
			break;
		}

		LayoutParams lp = new LayoutParams(10, 10);
		lp.setMargins(5, 0, 5, 0);
		dot.setLayoutParams(lp);
		pageNum.addView(dot, number);
	}

	private View newBtnPage(List<HashMap<String, Object>> FunctionMenuData) {
		GridView page = new GridView(this);
		// 一列最多幾個項目
		page.setNumColumns(4);
		// 把滑動的bar隱藏
		page.setScrollBarStyle(0);
		// 每列之間的高度兼具
		page.setVerticalSpacing(10);
		// 讓Gridview 不可以滑動
		page.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}

		});

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		page.setLayoutParams(lp);
		page.setAdapter(new MainItem(this, FunctionMenuData, ResolutionUtils.getPixelsHeight(this) / 800, ResolutionUtils.getPixelsWidth(this) / 480));

		return page;
	}

	private void setFunctionMenuData(int[] FunctionMenuIcon, String[] FunctionMenuText, Class[] Purpose) {
		if (FunctionMenuIcon == null || FunctionMenuText == null || Purpose == null) {
			return;
		}

		if (FunctionMenuIcon.length == 0 || FunctionMenuText.length == 0 || Purpose.length == 0) {
			return;
		}

		// icon 與 title 相同數量才會新增
		if (FunctionMenuText.length != FunctionMenuIcon.length && FunctionMenuIcon.length != Purpose.length) {
			return;
		}

		int totalPageNum = (FunctionMenuIcon.length - 1) / 8 + 2;

		/*
		 * 新增每一個FunctionMenu頁面及下方的頁碼 (黑點及藍點)
		 */
		for (int FunctionMenuPageDot = 1, PageDataMin = 0; FunctionMenuPageDot < totalPageNum; FunctionMenuPageDot++, PageDataMin += 8) {
			int PageDataMax = FunctionMenuPageDot * 8;
			if (FunctionMenuPageDot * 8 > FunctionMenuIcon.length) {
				PageDataMax = FunctionMenuIcon.length;
			}
			List<HashMap<String, Object>> FunctionMenuData = new ArrayList<HashMap<String, Object>>();
			for (int FunctionMenuPageData = PageDataMin; FunctionMenuPageData < PageDataMax; FunctionMenuPageData++) {
				HashMap<String, Object> index = new HashMap<String, Object>();
				index.put("title", FunctionMenuText[FunctionMenuPageData]);
				index.put("icon", FunctionMenuIcon[FunctionMenuPageData]);
				index.put("click", Purpose[FunctionMenuPageData]);
				FunctionMenuData.add(index);
			}

			functionPage.add(newBtnPage(FunctionMenuData));

			/*
			 * 新增下方頁碼初始化第一個點為藍色表示目前頁面位置
			 */
			if (FunctionMenuPageDot == 1) {
				addFunctionMenuPagDot(0, Color.BLUE);
			} else {
				addFunctionMenuPagDot(1, Color.BLACK);
			}
		}

		functionMenu.setAdapter(new ViewPagerAdapter(functionPage));
		functionMenu.setCurrentItem(0);
		functionMenu.setOnPageChangeListener(this);

	};

	private void findViews() {
		about = (TextView) findViewById(R.id.About);
		functionMenu = (ViewPager) findViewById(R.id.FunctionMenu);
		panel = (ViewFlipper) findViewById(R.id.Panel);
		pageNum = (LinearLayout) findViewById(R.id.FunctionMenuNumber);
	}

	private void init() {
		about.setText(getString(R.string.stu_name) + " | " + getString(R.string.stu_zipcode) + getString(R.string.stu_address) + " | TEL:"
				+ getString(R.string.stu_phnoe));
		functionPage = new ArrayList<View>();
		initFunctionMenuitem();
		initPanel();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findViews();
		init();
	}

	// 按鈕監聽事件
	public void movePage(View v) {
		Class Page = null;
		switch (v.getId()) {
		case R.id.news:
			Page = News.class;
			break;
		}

		if (Page != null) {
			Intent PageIntent = new Intent();
			PageIntent.setClass(this, Page);
			PageIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(PageIntent);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		pageNum.removeViewAt(CurrentPag);
		CurrentPag = arg0;
		addFunctionMenuPagDot(arg0, Color.BLUE);
	}

}