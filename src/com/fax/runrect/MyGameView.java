package com.fax.runrect;

import java.util.ArrayList;

import com.fax.runrect.R;
import com.fax.runrect.R.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

public class MyGameView extends SurfaceView implements SurfaceHolder.Callback {
	JumpRect jumpRect;
	Bitmap pic_floor;
	Bitmap pic_jumpRect;
	Bitmap pic_background;
	Bitmap pic_restart;
	Bitmap pic_fastrunair;
	DrawThread drawThread;

	int scores=0;
	int basescores=0;
	int addscores=0;
	int lastaddscores=0;
	public boolean isSendScores;
	
//	Handler myhandler;

	public MyGameView(Context context,AttributeSet attrs) {
		super(context,attrs);
		getHolder().addCallback(this);

		initBitmap(getResources());
		jumpRect = new JumpRect(0, 0,pic_jumpRect,this);
		drawThread = new DrawThread(getHolder(), this);

	}

	public void initBitmap(Resources r) {
		GameMap.initResources(getResources());
		pic_background = BitmapFactory.decodeResource(r, R.drawable.background);
		pic_jumpRect = BitmapFactory.decodeResource(r, R.drawable.rect);
		pic_restart = BitmapFactory.decodeResource(r, R.drawable.restart);
		pic_fastrunair=BitmapFactory.decodeResource(r, R.drawable.fastrunair);
		Obj.initBitmap(r);
	} 
	/**画出物体并移动，同时处理逻辑*/
	public void doDrawAndMove(Canvas canvas) {
		try {
			drawBkgd(canvas);
			drawFloors(canvas, true);
			drawObs(canvas, true);
			drawInfo(canvas, true);
			drawEatable(canvas, true);
			jumpRect.drawself(canvas);
		} catch (Exception e) {
		}
		Stage.distance += Stage.moveSpeed*Stage.moveSpeedScale;
	}
	/**画出物体不移动*/
	public void doDrawStatic(Canvas canvas) {
		drawBkgd(canvas);
		drawFloors(canvas,false);
		drawObs(canvas,false);
		drawInfo(canvas,false);
		drawEatable(canvas,false);
		jumpRect.drawself(canvas);
	}

	public void drawBkgd(Canvas canvas) {
		if(pic_background.getWidth()<getWidth()){
			Bitmap bitmap=Bitmap.createScaledBitmap(pic_background, getWidth(), pic_background.getHeight(), false);
			pic_background.recycle();
			pic_background=bitmap;
		}
		canvas.drawBitmap(pic_background, null,new Rect(0, 0, getWidth(), getHeight()), null);
//		canvas.drawBitmap(pic_background, 0,
//				Stage.bkgdMove - pic_background.getHeight(), null);
//		Stage.bkgdMove += 3;
//		if (Stage.bkgdMove >= pic_background.getHeight()) {
//			Stage.bkgdMove = 0;
//		}
	}
	public void drawInfo(Canvas canvas,boolean isMove) {
		ArrayList<InfoInView> templist=(ArrayList<InfoInView>) InfoInView.infoinviews.clone();
		int size=templist.size();
		for (int i=0;i<size;i++) {
			InfoInView o=templist.get(i);
			if (isMove) o.move();
			o.drawself(canvas);
		}
	}
	public void drawEatable(Canvas canvas,boolean isMove) {
		ArrayList<Eatable> templist=(ArrayList<Eatable>) Eatable.eatables.clone();
		int size=templist.size();
			for (int i=0;i<size;i++) {
				Eatable o=templist.get(i);
				
				if (isMove) o.move();

				if(o.y+o.width<0){
					o.delme();
					continue;
				}else if(o.y>Stage.gameViewWidth){
					continue;
				}
				
				o.drawself(canvas);
				
				if (!jumpRect.cantEat&&jumpRect.isNearMe(o)) {
					if (o.isHit(jumpRect)) {
						o.doHitCase(this, jumpRect);
						o.delme();
					}
				}
			}
	}
	
	public void drawObs(Canvas canvas,boolean isMove) {
		ArrayList<Obstacles> templist=(ArrayList<Obstacles>) Obstacles.obstacles.clone();
		int size=templist.size();
			for (int i=0;i<size;i++) {
				Obstacles o=templist.get(i);
				if (isMove) o.move();

					if(o.y+o.width<0){
						o.delme();
						continue;
					}else if(o.y>Stage.gameViewWidth){
						continue;
					}
					
					o.drawself(canvas);
					
				if (!jumpRect.cantObs&&jumpRect.isNearMe(o)) {
					if (o.isHit(jumpRect)) {
						Stage.gameOver();
					}
				}
			}
	}
	
	public void drawFloors(Canvas canvas,boolean isMove) {
		boolean isMeSave=false;
		ArrayList<Floor> templist=(ArrayList<Floor>) Floor.allfloors.clone();
		int size=templist.size();
			for (int i=0;i<size;i++) {
				Floor o=templist.get(i);
				if (isMove) o.move();
				if(o.y+o.width<0){
					o.delme();
					continue;
				}else if(o.y>Stage.gameViewWidth){
					continue;
				}
				o.drawself(canvas);
				if (!jumpRect.cantFloor&&jumpRect.isNearMe(o)) {
					if (o.isInUpArea(jumpRect)) {
						isMeSave=true;
						o.doUpCase(o.enterUpCase, jumpRect);
					} else if (o.isInDownArea(jumpRect)) {
						o.doDownCase(o.enterDownCase, jumpRect);
					} else if (o.isInLeftArea(jumpRect)) {
						o.doLeftCase(o.enterLeftCase, jumpRect);
					}
				}
			}
		if(jumpRect.walking&&!isMeSave){
			jumpRect.dump();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
			if (drawThread!=null) {
				Stage.setmyview(this);
				Stage.start(Stage.STAGE_ZERO);
			}else{
				drawThread = new DrawThread(getHolder(), this);
				drawThread.drawOneTime();
			}
			getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					drawThread.start();
					getViewTreeObserver().removeOnPreDrawListener(this);
					return false;
				}
			});
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
//		Stage.pause();
		drawThread.flag = false;
		drawThread = null;
//		Stage.clear();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!Stage.isPause) {
			jumpRect.jump();
		}
		return true;
	}

	public void getActivityHandler(Handler handler) {
		JumpRectangleActivity.handler=handler;
	}
}

class DrawThread extends Thread {
	SurfaceHolder surfaceHolder;
	MyGameView myview;
	boolean flag = true;
	JumpRect jumpRect;
	Canvas canvas = null;
	
	Matrix matrix;
	Paint paint;
	static int animationCount=0;
	
	int animSoundId;
	
	int fastrunl=150;
	
	public DrawThread(SurfaceHolder surfaceHolder, MyGameView myview) {
		this.surfaceHolder = surfaceHolder;
		this.myview = myview;
		jumpRect = myview.jumpRect;
	}
	public void drawOneTime(){

		canvas = surfaceHolder.lockCanvas();
		
		myview.doDrawAndMove(canvas);

		surfaceHolder.unlockCanvasAndPost(canvas);
	}
	public void run() {
		while (flag) {
			try {
				if (!Stage.isPause && DrawThread.animationCount == 0) {
					canvas = surfaceHolder.lockCanvas();
					if (jumpRect.jumping) {
						doJump(jumpRect.v0);
					} else if (jumpRect.dumping) {
						doJump(0);
					}
					if (jumpRect.x < 0) {
						Stage.gameOver();
					}
					if (Stage.nowStage <= 0) {
						creatMap();
					}
					myview.doDrawAndMove(canvas);
					checkFpsAndChangeSpeed(canvas);
					if (Stage.nowStage != Stage.STAGE_ZERO) {
						sendScores();
					}
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
				if (Stage.IsShowAnimation) {
					Stage.moveSpeedScale=1;
					canvas = surfaceHolder.lockCanvas();
					drawanimation();
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} catch (Exception e) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			try {
				if(Stage.isPause&&!Stage.IsShowAnimation){
					Thread.sleep(300);
					fpsCount=0;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void drawanimation(){
		animationCount++;
		switch(Stage.WhatAnimation){
		
		case Stage.ANIMATION_OBSTOSTAR:
			if(animationCount==1&&JumpRectangleActivity.soundonoff){
				animSoundId=JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_ObsChangeToostar, 1, 1, 5, 0, 1);
			}
			float scale=JumpRectangleActivity.scale;
			RectF rf=new RectF(Stage.gameViewHeight/2 -4* animationCount * scale,
						Stage.gameViewWidth/2 -4* animationCount * scale,
						Stage.gameViewHeight/2 + 4*animationCount * scale,
						Stage.gameViewWidth/2 +4* animationCount * scale);
			paint=new Paint();
			paint.setAlpha(255 - animationCount*4);
			myview.doDrawStatic(canvas); 
			canvas.drawBitmap(Star.bitmap, null,rf, paint);
		
			if(animationCount>50){
				JumpRectangleActivity.soundpool.stop(animSoundId);
				jumpRect.changeScreenObsToStar();
				Stage.stopAnimationAndPlay();
			}
		break;
		case Stage.ANIMATION_KILLOBS:
			if(animationCount==1&&JumpRectangleActivity.soundonoff){
				animSoundId=JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Thunder, 1, 1, 5, 0, 1);
			}
			if (animationCount%10>=5) {
				canvas.drawColor(Color.BLACK);
			}else{
				canvas.drawColor(Color.WHITE);
			}
			myview.drawObs(canvas,false);
			if(animationCount==60){
				jumpRect.clearScreenObs(Stage.nowStage<0);
			}
			if(animationCount>70){
				JumpRectangleActivity.soundpool.stop(animSoundId);
				Stage.stopAnimationAndPlay();
			}
		break;
		case Stage.ANIMATION_FASTRUN:
			if(animationCount==1){
				if (JumpRectangleActivity.soundonoff) {
					animSoundId = JumpRectangleActivity.soundpool.play(
							JumpRectangleActivity.SoundId_FastRun, 1, 1, 5, -1, 1);
				}
				fastrunl=JumpRectangleActivity.sp.getInt("fastrunl", 150);
				if(Stage.nowStage>0){
					fastrunl=150;
				}
				jumpRect.cantObs=true;
				jumpRect.cantFloor=true;
			}
			if(Stage.nowStage>0){
				animationCount+=2;
			}
			jumpRect.setAlpha(100);
			jumpRect.rotationAngle+=36;
			
			Stage.moveSpeed=2*Stage.defaultSpeed;
			myview.doDrawAndMove(canvas);
			float x=jumpRect.x;
			float y=jumpRect.y;
			float width=jumpRect.width;
			float height=jumpRect.height;
			RectF airArea=new RectF(x-height/3, y+width/2, x+height+height/3, y+width/2*3);
			canvas.drawBitmap(myview.pic_fastrunair,null,airArea, null);
			creatMap();
			sendScores();
			if(animationCount>=fastrunl){
				JumpRectangleActivity.soundpool.stop(animSoundId);
				Stage.initStageSpeed();
				jumpRect.rotationAngle=0;
				jumpRect.countAlphaTime=jumpRect.alphaTime-80;
				jumpRect.cantFloor=false;
				Stage.stopAnimationAndPlay();
			}
		break;
		default:Stage.stopAnimationAndPlay();break;
		}
		
		
	}
	
	public void sendScores(){
		int basescores=0;
		if (Stage.nowStage<0) {
			basescores = (int) (Stage.distance / JumpRectangleActivity.scale / 100);
		}
		if (basescores!=myview.basescores||myview.lastaddscores!=myview.addscores) {
			myview.basescores=basescores;
			myview.lastaddscores=myview.addscores;
			myview.scores=myview.basescores+myview.addscores;
//			Message m = myview.myhandler.obtainMessage(JumpRectangleGame.SCORES, myview.scores, 0);
			JumpRectangleActivity.handler.sendEmptyMessage(JumpRectangleActivity.MSG_SCORES);
		}
		
	}
	
	public void creatMap(){
		if (!Floor.allfloors.isEmpty()) {
			if(Stage.remarkFloor.y+Stage.remarkFloor.width<=Stage.gameViewWidth){
				Stage.addMap();
			}
		}
	}
	
	private void doJump(float v0) {
		float g=jumpRect.g;
		float scale=Stage.moveSpeed*Stage.moveSpeedScale/Stage.defaultSpeed;
		float spant=Stage.DrawRectDuration*scale;
		
		float lastT=jumpRect.recordTimer * spant;
		jumpRect.recordTimer++;
		float t = jumpRect.recordTimer * spant;
		jumpRect.v=v0+g*t;				
		float detaX=(v0 * t + g * t * t/2)-(v0 * lastT + g * lastT * lastT/2);
		if (jumpRect.lockJump) {
			detaX=0;
			if(jumpRect.v<0){
				jumpRect.lockJump=false;
			}
		}
		
//		detaX=detaX*highScale;//控制高度而不影响距离
		
		jumpRect.x +=detaX;
//		
//		float JumpT = 2.0f * v0 / jumpRect.g;
//		jumpRect.rotationAngle = Math.round(jumpRect.jumpRtAg * t / JumpT);
		jumpRect.rotationAngle +=13*scale;
		
//		if (jumpRect.x < Stage.start_x) {
//			jumpRect.jumping = false;
//			jumpRect.x = Stage.start_x;
//			jumpRect.rotationAngle = 0;
//			jumpRect.v=0;
//		}
	}

	long start;
	float fps = 0.0f;
	int fpsCount = 0;
	private static final float wantFps=40;
	private static final int FpsCountLimit=5;
	public void checkFpsAndChangeSpeed(Canvas canvas) {
		if(start==0) start= System.currentTimeMillis();
		this.fpsCount++;
		if (fpsCount == FpsCountLimit) { // 如果计满20帧
			fpsCount = 0; // 清空计数器
			long tempStamp = System.currentTimeMillis();// 获取当前时间
			long span = tempStamp - start; // 获取时间间隔
			start = tempStamp; // 为start重新赋值
			fps = Math.round(100000.0f / span * FpsCountLimit) / 100.0f;// 计算帧速率
			 Log.d("fax", fps+"");
			 Stage.moveSpeedScale=wantFps/fps;
			 if(Stage.moveSpeedScale<0.7f) Stage.moveSpeedScale=0.7f;
			 if(Stage.moveSpeedScale>1.5f) Stage.moveSpeedScale=1.5f;
		}
//		drawInfo(canvas, jumpRect, fps);
	}
	private static TextPaint textPaint;
	static{
		textPaint = new TextPaint();
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(16*JumpRectangleActivity.scale);
	}
	public void drawInfo(Canvas canvas, JumpRect jumpRect, double fps) {
//		canvas.drawLine(Stage.atColumn(2)*JumpRectangleGame.scale, 0, Stage.atColumn(2)*JumpRectangleGame.scale,
//				Stage.windowsWidth, textPaint);
//		canvas.drawLine(Stage.atColumn(3)*JumpRectangleGame.scale, 0, Stage.atColumn(3)*JumpRectangleGame.scale,
//				Stage.windowsWidth, textPaint);
//		canvas.drawLine(Stage.atColumn(4)*JumpRectangleGame.scale, 0, Stage.atColumn(4)*JumpRectangleGame.scale,
//				Stage.windowsWidth, textPaint);
		
		StringBuffer s=new StringBuffer();
//		s.append(" 位置：" + jumpRect.x / JumpRectangleGame.scale);
//		s.append(" 跳跃：" + jumpRect.jumping);
//		s.append(" 角度：" + jumpRect.rotationAngle);
//		s.append(" 跳跃时间：" + jumpRect.recordTimer);
		s.append(" FPS：" + fps);
//		s.append(" 距离：" + Stage.distance / JumpRectangleGame.scale);
		s.append(" 障：" + Obstacles.obstacles.size());
		s.append(" 地：" + Floor.allfloors.size());
		s.append(" 吃：" + Eatable.eatables.size());
//		s.append(" 垂直速度：" + jumpRect.v);
		Message m=JumpRectangleActivity.handler.obtainMessage(JumpRectangleActivity.MSG_INFO, s);
		JumpRectangleActivity.handler.sendMessage(m);
		
		canvas.drawText(s.toString(), 0, 20+textPaint.getTextSize(), textPaint);
	}
}