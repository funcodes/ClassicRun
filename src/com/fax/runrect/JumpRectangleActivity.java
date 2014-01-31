package com.fax.runrect;


import com.fax.runrect.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class JumpRectangleActivity extends Activity{
	MyGameView myview;
	static float scale;
	FrameLayout lay_bgfl;//游戏顶层容器
	ImageButton but_pause;
	LinearLayout lay_attack;
	ImageButton but_attack1;
	TextView scoreText;
	TextView highscoreText;
	TextView infoText;
	
	LinearLayout lay_frontll;//游戏之上的界面层
	
	LinearLayout lay_custom;
	static int but1_animation;
	static int but2_animation;
	
	static SharedPreferences sp;
	
	static final int MSG_GAMEOVER=1;
	static final int MSG_SCORES=2;
	static final int MSG_INFO=3;
	static final int MSG_SHOWTEXT=4;
	static final int MSG_ADDATTACK=5;
	static final int MSG_NEXTSTAGE=6;
	static final int MSG_LOADED=7;
	
	MediaPlayer backgroundmusic;
	static boolean musiconoff=true;
	static boolean soundonoff=true;
	static SoundPool soundpool;
	static int SoundId_Click;
	static int SoundId_Change;
	static int SoundId_ChangeRestore;
	static int SoundId_FastRun;
	static int SoundId_Thunder;
	static int SoundId_ObsChangeToostar;
	static int SoundId_Star;
	static int SoundId_Eat;
	static int SoundId_GameWin;
	static int SoundId_GameOver;
	static Handler handler;
	static final int DefualtPoint=300;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scale = this.getResources().getDisplayMetrics().density;
		Stage.gameViewWidth = this.getResources().getDisplayMetrics().heightPixels;
		Stage.gameViewHeight = this.getResources().getDisplayMetrics().widthPixels;
		
		super.setContentView(R.layout.activity_main);
		sp=getPreferences(MODE_PRIVATE);
		
		initSound();
		
		lay_bgfl = (FrameLayout) findViewById(R.id.fl);
		lay_attack=(LinearLayout)findViewById(R.id.lay_attack);
		but_pause = (ImageButton) findViewById(R.id.but_pause);
		but_attack1=(ImageButton) findViewById(R.id.but_attack1);
		scoreText=(TextView)findViewById(R.id.score_text);
		highscoreText=(TextView)findViewById(R.id.highscore_text);
		infoText=(TextView)findViewById(R.id.info_text);
		
		lay_frontll = (MyLinearLayout) findViewById(R.id.lay_frontll);
		myview = (MyGameView) findViewById(R.id.mygameview);
		Stage.myview=myview;

		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case MSG_GAMEOVER:
					if (!Stage.IsShowAnimation) {
						gameover();
					}
					break;
				case MSG_NEXTSTAGE:
					nextstage();
					break;
				case MSG_LOADED:
					removeloadingview();
					break;
				case MSG_SCORES:
					scoreText.setText(myview.scores+"");
					break;
				case MSG_INFO:
					infoText.setText((CharSequence) msg.obj);
					break;
				case MSG_SHOWTEXT:
					if (Stage.nowStage==0) {
						disshowtext();
					}else{
						showtext();
					}
					break;
				case MSG_ADDATTACK:
					if(but_attack1.getVisibility()==View.GONE){
						but_attack1.setBackgroundResource(msg.arg1);
						but1_animation=msg.arg2;
						but_attack1.setVisibility(View.VISIBLE);
					}else if(lay_attack.getChildCount()<sp.getInt("attackcount", 1)-1){
						ImageView ib=new ImageView(JumpRectangleActivity.this);
						ib.setBackgroundResource(msg.arg1);
						ib.setTag(""+msg.arg2);
//						ib.setAdjustViewBounds(true);
//						ib.setMaxHeight((int) (scale*10));
//						ib.setMaxWidth((int) (scale*10));
						lay_attack.addView(ib, 0,new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					}
				break;
				}
			}

			
		};

		disclickable();
		disshowtext();
		setFrontView(R.layout.welcome_view);

		musiconoff=sp.getBoolean("musiconoff", true);
		soundonoff=sp.getBoolean("soundonoff", true);
		if (musiconoff) {
			backgroundmusic.start();
		}
	}
	private void initSound(){
		backgroundmusic=MediaPlayer.create(this, R.raw.background);
		backgroundmusic.setLooping(true);
		soundpool=new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		SoundId_Click=soundpool.load(this, R.raw.click, 1);
		SoundId_Change=soundpool.load(this, R.raw.change, 1);
		SoundId_ChangeRestore=soundpool.load(this, R.raw.change_restore, 1);
		SoundId_FastRun=soundpool.load(this, R.raw.fastrun, 1);
		SoundId_Thunder=soundpool.load(this, R.raw.sd, 1);
		SoundId_ObsChangeToostar=soundpool.load(this, R.raw.obschangetostar, 1);
		SoundId_Star=soundpool.load(this, R.raw.star, 1);
		SoundId_Eat=soundpool.load(this, R.raw.eat, 1);
		SoundId_GameWin=soundpool.load(this, R.raw.gamewin, 1);
		SoundId_GameOver=soundpool.load(this, R.raw.gameover, 1);
	}

	//通过tag信息显示界面
	public void change_view_by_tag(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		String tag=view.getTag().toString();
		try {
			setFrontView(Integer.valueOf(tag));
			return;
		} catch (Exception e) {
		}
		String value=tag.substring(11, tag.length()-4);
		Class<R.layout> layoutClass=R.layout.class;
		try {
			int resId=(Integer) layoutClass.getDeclaredField(value).get(new R.layout());
			setFrontView(resId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**设置界面层的显示内容*/
	public void setFrontView(int layoutResID) {
		while(lay_frontll.getChildCount()>0){//删除所有子view
			lay_frontll.removeView(lay_frontll.getChildAt(0));
		}
		if(layoutResID>=0) lay_frontll.addView(createView(layoutResID));
		
		if(Stage.isPause){//处理页面变化时，背景游戏重开
			switch(layoutResID){
			case R.layout.welcome_view:
			case R.layout.usepoint_view:
				Stage.start(Stage.STAGE_ZERO);//界面显示的时候将游戏重新启动
			}
		}
	}
	private void addFrontView(int layoutResID){
		lay_frontll.addView(createView(layoutResID));
	}
	private View createView(int layoutResID){//创建view，同时修改一些内容
		View view=View.inflate(this, layoutResID, null);
		switch(layoutResID){
		case R.layout.difficulty_select:
			changeView_DifSelect(view);
			break;
		case R.layout.stage_select:
			changeView_StageSelect(view);
			break;
		case R.layout.winpoint_view:
			changeView_Winpoint((ViewGroup) view);
			break;
		case R.layout.usepoint_view:
			changeView_usepoint(view);
			break;
		case R.layout.setting_view:
			changeView_setting(view);
			break;
		}
		return view;
	}
	private void changeView_setting(View view){
		if(!musiconoff){
			ImageButton ib=(ImageButton)(lay_frontll.findViewById(R.id.but_bgmusic_onoff));
			ib.setImageResource(R.drawable.music_off);
		}
		if(!soundonoff){
			ImageButton ib=(ImageButton)(lay_frontll.findViewById(R.id.but_gamemusic_onoff));
			ib.setImageResource(R.drawable.sound_off);
		}
	}
	
	private void changeView_DifSelect(View view){
		if(sp.getInt("locknormal", 0)!=0){
			view.findViewById(R.id.but_stage_normal_lock).setVisibility(View.GONE);
		}
		if(sp.getInt("lockhard", 0)!=0){
			view.findViewById(R.id.but_stage_hard_lock).setVisibility(View.GONE);
		}
		if(sp.getInt("lockcustom", 0)!=0){
			view.findViewById(R.id.but_stage_custom_lock).setVisibility(View.GONE);
		}
		
	}
	private void changeView_StageSelect(View view){
		LinearLayout templl=(LinearLayout) (view.findViewById(R.id.lay_stage_select));
		if(templl==null) return;
		int count=templl.getChildCount();
		for(int i=0;i<count;i++){
			ImageButton ib=(ImageButton) templl.getChildAt(i);
			if(sp.getInt("stage"+(i+1), 0)!=0){
				ib.getBackground().setLevel(getHighScore(i+1));
			}else{
				ib.getBackground().setLevel(1000);
			}
		}
	}

	private void changeView_usepoint(View lay_usepoint){
		setIntInLay(sp.getInt("point", DefualtPoint),(LinearLayout) lay_usepoint.findViewById(R.id.lay_nowpoint));
		
		setIntInLay(sp.getInt("changeAlphaAddTime", 0)+300, (LinearLayout) lay_usepoint.findViewById(R.id.state_addalphatime));
		setIntInLay((int)(sp.getInt("changeAlphaAddTime", 0)/3+30), (LinearLayout) lay_usepoint.findViewById(R.id.addalphatime_spend));
		setIntInLay(sp.getInt("changeSmallAddTime", 0)+300, (LinearLayout) lay_usepoint.findViewById(R.id.state_addsmalltime));
		setIntInLay((int)(sp.getInt("changeSmallAddTime", 0)/3+30), (LinearLayout) lay_usepoint.findViewById(R.id.addsmalltime_spend));
		setIntInLay(Math.max(300-sp.getInt("changeBigSubTime", 0),0), (LinearLayout) lay_usepoint.findViewById(R.id.state_subbigtime));
		setIntInLay((int)(sp.getInt("changeBigSubTime", 0)/3+30), (LinearLayout) lay_usepoint.findViewById(R.id.subbigtime_spend));
		setIntInLay(sp.getInt("fastrunl", 150), (LinearLayout) lay_usepoint.findViewById(R.id.state_runfastl));
		setIntInLay((int)(sp.getInt("fastrunl", 150)/3), (LinearLayout) lay_usepoint.findViewById(R.id.fastrun_spend));
		setIntInLay(sp.getInt("attackcount", 1), (LinearLayout) lay_usepoint.findViewById(R.id.state_addac));
		setIntInLay(sp.getInt("attackcount", 1)*50+50, (LinearLayout) lay_usepoint.findViewById(R.id.addac_spend));
	}
	private void changeView_Winpoint(ViewGroup lay_winpoint){
		int size=lay_winpoint.getChildCount();
		int basescores=myview.scores;
		if(Stage.nowStage>0){
			basescores=basescores*100;
		}
		int scorecount=0;
		for(int i=size-1;i>=0;i--){
			LinearLayout l=(LinearLayout) lay_winpoint.getChildAt(i);
			if(l.getTag().equals("基础得分")){
				setIntInLay(basescores, l);
				scorecount+=basescores;
			}else if(l.getTag().equals("跳跃次数")){
				setIntInLay(myview.jumpRect.jumpcount, l);
				scorecount+=myview.jumpRect.jumpcount;
			}else if(l.getTag().equals("破纪录")){
				if (myview.scores>getHighScore()) {
					l.setVisibility(View.VISIBLE);
					setIntInLay(basescores, l);
					scorecount+=basescores;
				}else{
					l.setVisibility(View.GONE);
				}
			}else if(l.getTag().equals("总计")){
				setIntInLay(scorecount, l);
			}else if(l.getTag().equals("获得积分")){
				int addpoint=(int) (scorecount/50);
				setIntInLay(addpoint, l);
				awardpoint(addpoint);
			}
		}
	}
	private int getIntInLay(LinearLayout l){
		int s=0;
		ImageView iv=(ImageView) l.getChildAt(1);
		if(iv.getVisibility()==View.VISIBLE){
			s+=iv.getDrawable().getLevel()*1000;
		}
		iv=(ImageView) l.getChildAt(2);
		if(iv.getVisibility()==View.VISIBLE){
			s+=iv.getDrawable().getLevel()*100;
		}
		iv=(ImageView) l.getChildAt(3);
		if(iv.getVisibility()==View.VISIBLE){
			s+=iv.getDrawable().getLevel()*10;
		}
		iv=(ImageView) l.getChildAt(4);
		if(iv.getVisibility()==View.VISIBLE){
			s+=iv.getDrawable().getLevel();
		}
		return s;
	}
	private void setIntInLay(int number,LinearLayout l){
		String s=number+"";
		int slength=s.length();
		((ImageView) l.getChildAt(1)).setVisibility(View.VISIBLE);
		((ImageView) l.getChildAt(2)).setVisibility(View.VISIBLE);
		((ImageView) l.getChildAt(3)).setVisibility(View.VISIBLE);
		((ImageView) l.getChildAt(4)).setVisibility(View.VISIBLE);
		if(slength==4){
			((ImageView) l.getChildAt(1)).getDrawable().setLevel(Integer.valueOf(s.substring(0, 1)));
			((ImageView) l.getChildAt(2)).getDrawable().setLevel(Integer.valueOf(s.substring(1, 2)));
			((ImageView) l.getChildAt(3)).getDrawable().setLevel(Integer.valueOf(s.substring(2, 3)));
			((ImageView) l.getChildAt(4)).getDrawable().setLevel(Integer.valueOf(s.substring(3, 4)));
		}else if(slength==3){
			l.getChildAt(1).setVisibility(View.INVISIBLE);
			((ImageView) l.getChildAt(2)).getDrawable().setLevel(Integer.valueOf(s.substring(0, 1)));
			((ImageView) l.getChildAt(3)).getDrawable().setLevel(Integer.valueOf(s.substring(1, 2)));
			((ImageView) l.getChildAt(4)).getDrawable().setLevel(Integer.valueOf(s.substring(2, 3)));
		}else if(slength==2){
			l.getChildAt(1).setVisibility(View.INVISIBLE);
			l.getChildAt(2).setVisibility(View.INVISIBLE);
			((ImageView) l.getChildAt(3)).getDrawable().setLevel(Integer.valueOf(s.substring(0, 1)));
			((ImageView) l.getChildAt(4)).getDrawable().setLevel(Integer.valueOf(s.substring(1, 2)));
		}else if(slength==1){
			l.getChildAt(1).setVisibility(View.INVISIBLE);
			l.getChildAt(2).setVisibility(View.INVISIBLE);
			l.getChildAt(3).setVisibility(View.INVISIBLE);
			((ImageView) l.getChildAt(4)).getDrawable().setLevel(Integer.valueOf(s.substring(0, 1)));
		}
	}
	public void gameover(){
		showFrontll();
		lay_frontll.removeAllViews();
		if (Stage.nowStage<0) {
			addFrontView(R.layout.winpoint_view);
			writeHighScore();
			showGameHighScoreInfo();
		}
		addFrontView(R.layout.gameover_view);
	}
	public void nextstage(){
		showFrontll();
		if (myview.scores>getHighScore()) {
			addFrontView(R.layout.winpoint_view);
			writeHighScore();
			showGameHighScoreInfo();
		}
		addFrontView(R.layout.stagewin_view);
	}
	private void writeHighScore(){
		SharedPreferences.Editor editor =sp.edit();
		if (myview.scores>getHighScore()) {
			switch (Stage.nowStage) {
			case Stage.STAGE_EASY:
				editor.putInt("HighScore_Easy", myview.scores);
				break;
			case Stage.STAGE_NORMAL:
				editor.putInt("HighScore_Normal", myview.scores);
				break;
			case Stage.STAGE_HARD:
				editor.putInt("HighScore_Hard", myview.scores);
				break;
			case Stage.STAGE_CUSTOM:break;
			default:editor.putInt(Stage.nowStage+"", myview.scores);
				break;
			}
			editor.commit();
		}
	}
	private int getHighScore() {
		int result=0;
		switch(Stage.nowStage){
		case Stage.STAGE_EASY:result =sp.getInt("HighScore_Easy", 0);break;
		case Stage.STAGE_NORMAL:result =sp.getInt("HighScore_Normal", 0);break;
		case Stage.STAGE_HARD:result =sp.getInt("HighScore_Hard", 0);break;
		case Stage.STAGE_CUSTOM:break;
		default:result =sp.getInt(Stage.nowStage+"", 0);break;
		}
		return result;
	}
	private int getHighScore(int stage) {
		int result =sp.getInt(stage+"", 0);
		return result;
	}
	public void clickable(){
		but_pause.setVisibility(View.VISIBLE);
	}
	public void disclickable(){
		but_pause.setVisibility(View.GONE);
	}
	public void showtext(){
		scoreText.setVisibility(View.VISIBLE);
		highscoreText.setVisibility(View.VISIBLE);
		infoText.setVisibility(View.VISIBLE);
	}
	public void disshowtext(){
		scoreText.setVisibility(View.GONE);
		highscoreText.setVisibility(View.GONE);
		infoText.setVisibility(View.GONE);
		but_attack1.setVisibility(View.GONE);
		lay_attack.removeAllViews();
	}
	private void showDialog(final String key,final int usepoint){
		final View lay_dialog=View.inflate(this, R.layout.usepoint_dialog_view, null);
		lay_bgfl.addView(lay_dialog);
		refreshPointInLayout((LinearLayout) lay_dialog.findViewById(R.id.lay_nowpoint));
		setIntInLay(usepoint, (LinearLayout)lay_dialog.findViewById(R.id.lay_dialog_usepoint));
		((ImageButton)lay_dialog.findViewById(R.id.but_yes)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if(spendpoint(usepoint)){
					SharedPreferences.Editor editor = sp.edit();
					String s = key;
					editor.putInt(s, 1);
					editor.commit();
					if (s.equals("locknormal") || s.equals("lockhard") || s.equals("lockcustom")) {
						changeView_DifSelect(lay_frontll);
					} else {
						changeView_StageSelect(lay_frontll);
					}
					lay_bgfl.removeView(lay_dialog);
				}
			}
		});
		((ImageButton)lay_dialog.findViewById(R.id.but_no)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				lay_bgfl.removeView(lay_dialog);
			}
		});
	}
	private boolean spendpoint(int spend){
		int n=sp.getInt("point", DefualtPoint);
		if(n>=spend){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("point", n-spend);
			editor.commit();
			return true;
		}
		return false;
	}
	private void awardpoint(int award){
		int n=sp.getInt("point", DefualtPoint);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("point", n + award);
		editor.commit();
	}
		public void click_difcmode(View view){//开始休闲模式
			soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
			String s=(String)(view.getTag());
			if( s.equals("easy")){
				lay_frontll.removeView((View) view.getParent());
				dismissFrontll();
				Stage.start(Stage.STAGE_EASY);
			}else if( s.equals("hard")){
				if (sp.getInt("lockhard", 0)!=0) {
					lay_frontll.removeView((View) view.getParent());
					dismissFrontll();
					Stage.start(Stage.STAGE_HARD);
				}else{
					showDialog("lockhard", 30);
				}
			}else if( s.equals("normal")){
				if (sp.getInt("locknormal", 0)!=0) {
					lay_frontll.removeView((View) view.getParent());
					dismissFrontll();
					Stage.start(Stage.STAGE_NORMAL);
				}else{
					showDialog("locknormal", 20);
				}
			}else if(s.equals("custom")){
				if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_3rect_0)).isChecked()) Stage.Custom3RectSet=0;
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_3rect_1)).isChecked()) Stage.Custom3RectSet=1;
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_3rect_2)).isChecked()) Stage.Custom3RectSet=2;
				if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_speed_1)).isChecked()) Stage.CustomSpeed=Stage.convert(3);
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_speed_2)).isChecked()) Stage.CustomSpeed=Stage.convert(4);
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_speed_3)).isChecked()) Stage.CustomSpeed=Stage.convert(5);
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_speed_4)).isChecked()) Stage.CustomSpeed=Stage.convert(6);
				if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_obsnum_1)).isChecked()) Stage.WidthToCreatObs=300;
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_obsnum_2)).isChecked()) Stage.WidthToCreatObs=250;
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_obsnum_3)).isChecked()) Stage.WidthToCreatObs=200;
				else if(((RotateableButton)lay_frontll.getChildAt(0).findViewById(R.id.text_custom_obsnum_4)).isChecked()){
					Stage.WidthToCreatObs=200;
					Stage.CustomObsAdd=true;
				}
				
				setFrontView(-1);
				dismissFrontll();
				Stage.start(Stage.STAGE_CUSTOM);
			}else if(s.equals("showcustom")){
				if (sp.getInt("lockcustom", 0)!=0) {
					setFrontView(R.layout.custom_view);
				}else{
					showDialog("lockcustom", 100);
				}
			}
			showGameHighScoreInfo();
		}
	public void click_stagemode(View view){//开始关卡模式
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		String s=view.getTag().toString();
		if (sp.getInt("stage"+s, 0)!=0) {
			setFrontView(-1);
			addloadingview();
			int n = Integer.valueOf(s);
			Stage.start(n);
			showGameHighScoreInfo();
		}else{
			showDialog("stage"+s, 30);
		}
	}
	private void refreshPointInLayout(LinearLayout layout){
		setIntInLay(sp.getInt("point", DefualtPoint),layout);
	}
	
	public void click_fastrunl(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		int spend=getIntInLay((LinearLayout) lay_frontll.findViewById(R.id.fastrun_spend));

		if(spendpoint(spend)){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("fastrunl", sp.getInt("fastrunl", 150)+30);
			editor.commit();
			setIntInLay(sp.getInt("fastrunl", 150), (LinearLayout) lay_frontll.findViewById(R.id.state_runfastl));
			setIntInLay((int)(sp.getInt("fastrunl", 150)/3), (LinearLayout) lay_frontll.findViewById(R.id.fastrun_spend));

			refreshPointInLayout((LinearLayout) lay_frontll.findViewById(R.id.lay_nowpoint));
		}
	}
	public void click_subbigtime(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		int spend=getIntInLay((LinearLayout) lay_frontll.findViewById(R.id.subbigtime_spend));

		if(spendpoint(spend)){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("changeBigSubTime", sp.getInt("changeBigSubTime", 0)+30);
			editor.commit();
			setIntInLay(Math.max(300-sp.getInt("changeBigSubTime", 0),0), (LinearLayout) lay_frontll.findViewById(R.id.state_subbigtime));
			setIntInLay((int)(sp.getInt("changeBigSubTime", 0)/3+30), (LinearLayout) lay_frontll.findViewById(R.id.subbigtime_spend));

			refreshPointInLayout((LinearLayout) lay_frontll.findViewById(R.id.lay_nowpoint));
		}
	}
	public void click_addsmalltime(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		int spend=getIntInLay((LinearLayout) lay_frontll.findViewById(R.id.addsmalltime_spend));

		if(spendpoint(spend)){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("changeSmallAddTime", sp.getInt("changeSmallAddTime", 0)+30);
			editor.commit();
			setIntInLay(sp.getInt("changeSmallAddTime", 0)+300, (LinearLayout) lay_frontll.findViewById(R.id.state_addsmalltime));
			setIntInLay((int)(sp.getInt("changeSmallAddTime", 0)/3+30), (LinearLayout) lay_frontll.findViewById(R.id.addsmalltime_spend));

			refreshPointInLayout((LinearLayout) lay_frontll.findViewById(R.id.lay_nowpoint));
		}
	}
	public void click_addalphatime(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		int spend=getIntInLay((LinearLayout) lay_frontll.findViewById(R.id.addalphatime_spend));

		if(spendpoint(spend)){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("changeAlphaAddTime", sp.getInt("changeAlphaAddTime", 0)+30);
			editor.commit();
			setIntInLay(sp.getInt("changeAlphaAddTime", 0)+300, (LinearLayout) lay_frontll.findViewById(R.id.state_addalphatime));
			setIntInLay((int)(sp.getInt("changeAlphaAddTime", 0)/3+30), (LinearLayout) lay_frontll.findViewById(R.id.addalphatime_spend));

			refreshPointInLayout((LinearLayout) lay_frontll.findViewById(R.id.lay_nowpoint));
		}
	}
	public void click_addattackcount(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		int spend=getIntInLay((LinearLayout) lay_frontll.findViewById(R.id.addac_spend));

		if(spendpoint(spend)){
			SharedPreferences.Editor editor=sp.edit();
			editor.putInt("attackcount", sp.getInt("attackcount", 1)+1);
			editor.commit();
			setIntInLay(sp.getInt("attackcount", 1), (LinearLayout) lay_frontll.findViewById(R.id.state_addac));
			setIntInLay(sp.getInt("attackcount", 1)*50+50, (LinearLayout) lay_frontll.findViewById(R.id.addac_spend));

			refreshPointInLayout((LinearLayout) lay_frontll.findViewById(R.id.lay_nowpoint));
		}
	}
	public void click_bgmusic_onoff(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		SharedPreferences.Editor editor=sp.edit();
		if(musiconoff){
			musiconoff=false;
			editor.putBoolean("musiconoff", false);
			backgroundmusic.pause();
			((ImageButton) view).setImageResource(R.drawable.music_off);
		}else{
			musiconoff=true;
			editor.putBoolean("musiconoff", true);
			backgroundmusic.start();
			((ImageButton) view).setImageResource(R.drawable.music_on);
		}
		editor.commit();
	}
	public void click_gamemusic_onoff(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		SharedPreferences.Editor editor=sp.edit();
		if(soundonoff==true){
			soundonoff=false;
			editor.putBoolean("soundonoff", false);
			((ImageButton) view).setImageResource(R.drawable.sound_off);
		}else{
			soundonoff=true;
			editor.putBoolean("soundonoff", true);
			((ImageButton) view).setImageResource(R.drawable.sound_on);
		}
		editor.commit();
	}
	public void click_pause(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		showFrontll();
		setFrontView(R.layout.pause_view);
	}
	public void click_resume(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		lay_frontll.removeView((View) view.getParent());
		dismissFrontll();
	}
	public void click_restart(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		setFrontView(-1);
		if (Stage.nowStage<0) {
			dismissFrontll();
		}else if (Stage.nowStage>0){
			addloadingview();
		}
		Stage.restart();
		but_attack1.setVisibility(View.GONE);
		lay_attack.removeAllViews();
	}
	public void click_nextstage(View view){
		soundpool.play(SoundId_Click, 1, 1, 10, 0, 1);
		if (sp.getInt("stage"+(Stage.nowStage+1), 0)!=0) {
			setFrontView(-1);
			addloadingview();
			Stage.start(Stage.nowStage + 1);
			but_attack1.setVisibility(View.GONE);
			lay_attack.removeAllViews();
			showGameHighScoreInfo();
		}else{
			View v=new View(this);
			v.setTag((Stage.nowStage+1)+"");
			click_stagemode(v);
		}
	}
	public void showGameHighScoreInfo(){
		switch(Stage.nowStage){
		case Stage.STAGE_EASY:highscoreText.setText("简单："+getHighScore());break;
		case Stage.STAGE_NORMAL:highscoreText.setText("普通："+getHighScore());break;
		case Stage.STAGE_HARD:highscoreText.setText("困难："+getHighScore());break;
		case Stage.STAGE_CUSTOM:highscoreText.setText("自定义难度");break;
		default:highscoreText.setText("关卡"+Stage.nowStage+":"+getHighScore());break;
		}
	}
	
	public void click_attack1(View view){//发技能
		if (!Stage.isPause&&!Stage.IsShowAnimation) {
			Stage.pause();
			Stage.startAnimation(but1_animation);
			int count=lay_attack.getChildCount();
			if (count>0) {
				ImageView iv=(ImageView) lay_attack.getChildAt(count-1);
				view.setBackgroundDrawable(iv.getBackground());
				but1_animation=Integer.valueOf(iv.getTag().toString());
				lay_attack.removeView(iv);
			} else {
				view.setVisibility(View.GONE);
			}
		}
	}
	public void addloadingview(){
		setFrontView(R.layout.loading_view);
	}
	public void removeloadingview(){
		setFrontView(-1);
		dismissFrontll();
	}
	private void showFrontll(){
			disclickable();
			Stage.pause();
			lay_frontll.setVisibility(View.VISIBLE);
			lay_frontll.removeAllViews();
	}
	public void dismissFrontll(){
		Animation a=new AlphaAnimation(1.0f, 0);
		a.setDuration(10);
		a.setStartOffset(300);
		lay_frontll.startAnimation(a);
		a.setAnimationListener(new RemoveLayFrontllImp());
	}
	class RemoveLayFrontllImp implements AnimationListener{
		public void onAnimationEnd(Animation animation) {
			lay_frontll.setVisibility(View.INVISIBLE);
			clickable();
			Stage.play();
		}
		public void onAnimationRepeat(Animation animation) {
		}
		public void onAnimationStart(Animation animation) {
		}
	}
	
	@Override   
    public boolean onKeyDown(int keyCode, KeyEvent event) {    
        if(keyCode==KeyEvent.KEYCODE_BACK) {
        	View backBtn=lay_frontll.findViewById(R.id.but_back);
        	if(backBtn!=null){
        		backBtn.performClick();
    			return true;
        	}
        }
       return super.onKeyDown(keyCode, event); 
    }
	@Override
	protected void onPause() {
		backgroundmusic.pause();
		if (Stage.nowStage!=Stage.STAGE_ZERO&&lay_bgfl.indexOfChild(lay_frontll)==-1) {
			click_pause(but_pause);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(musiconoff){
			backgroundmusic.start();
		}
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
}


class MyLinearLayout extends LinearLayout{

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public void addView(View child) {
		if(child.getParent()==null){
			super.addView(child);
			Animation a=new TranslateAnimation(0, 0, (-1)*Stage.convert(480), 0);
			a.setDuration(500);
			child.startAnimation(a);
		}
	}

	@Override
	public void removeView(View view) {
		Animation a=new TranslateAnimation(0, 0, 0, Stage.convert(480));
		a.setDuration(300);
		view.startAnimation(a);
		super.removeView(view);
	}  
}
class RotateableButton extends RadioButton{  
	  
    public RotateableButton(Context context) {  
        super(context);
        this.setButtonDrawable(null);
    }
    public RotateableButton(Context context, AttributeSet attrs) {  
        super(context, attrs);
        this.setButtonDrawable(null);
    }  
    @Override  
    protected void onDraw(Canvas canvas) {
        canvas.translate(getHeight()*5/6, 0);
        canvas.rotate(90);  
        super.onDraw(canvas);  
    }
}  


class RotateableTextView extends TextView{  
	  
    public RotateableTextView(Context context) {  
        super(context);
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
    }
    public RotateableTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
    }  
    @Override  
    protected void onDraw(Canvas canvas) {
        canvas.translate(getHeight()*5/6, 0);  
        canvas.rotate(90);
        super.onDraw(canvas);
    }
} 