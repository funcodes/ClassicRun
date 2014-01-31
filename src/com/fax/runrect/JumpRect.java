package com.fax.runrect;

import java.util.Collections;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class JumpRect{
	float x;
	float y;
	float v0=60*JumpRectangleActivity.scale;
	float g=-20*JumpRectangleActivity.scale;
	double v=0;
	int rotationAngle;//旋转角
	float jumpRtAg=1*360;
	float height = 30*JumpRectangleActivity.scale;
	float width = 30*JumpRectangleActivity.scale;
	int alpha=255;
	float diagonal=width*1.4f;//对角线长（近似）
//	float offset=(diagonal-height)/2;//对角线与高的差得一半
	
	int jumpcount=0;
	
	boolean jumping = false;
	boolean walking =true;
	boolean dumping =true;
	float sx;
	float sy;
	int countCantHitTime=0;
	int countAlphaTime=0;
	int alphaTime=300;
	int countScaleTime=0;
	int scaleTime=300;
	
	boolean cantObs=false;
	boolean cantEat=false;
	boolean cantFloor=false;
	boolean cantAttack=false;
	
	
	Bitmap bitmap;
	int recordTimer;
	float recordX;
	boolean lockJump=false;
	float lockJumpX=Stage.gameViewHeight;
	public boolean isMeSave=false;
	Paint p;
	MyGameView myview;
	public JumpRect(float x,float y, Bitmap bitmap,MyGameView myview) {
		this.x = x;
		this.y = y;
		this.rotationAngle = 0;
		this.bitmap = bitmap;
		p=new Paint();
		this.myview=myview;
	}

	public void drawself(Canvas canvas) {
		Matrix m = new Matrix();
		sx=width / Stage.convert(30);
		sy=height / Stage.convert(30);
		if (sx!=1||sy!=1) {
			m.postScale(sx, sy);
			if (countScaleTime==0) {
				if (sx > 1 && sy > 1) {
					scaleTime=300-JumpRectangleActivity.sp.getInt("changeBigSubTime",0);
				} else if (sx < 1 && sy < 1) {
					scaleTime=300+JumpRectangleActivity.sp.getInt("changeSmallAddTime",0);
				}
			}
			if (!Stage.IsShowAnimation) {
				countScaleTime++;
			}
			if(countScaleTime>=scaleTime){
				if (JumpRectangleActivity.soundonoff) {
					JumpRectangleActivity.soundpool.play(
							JumpRectangleActivity.SoundId_ChangeRestore, 1, 1, 5, 0,
							1);
				}
				width=Stage.convert(30);
				height=Stage.convert(30);
			}
		}
		if(alpha!=255){
			if (countAlphaTime==0) {
				alphaTime=300+JumpRectangleActivity.sp.getInt("changeAlphaAddTime",0);
			}
			if (!Stage.IsShowAnimation) {
				countAlphaTime++;
			}
			if(countAlphaTime>alphaTime-100){
				if (countAlphaTime%20>=10) {
					alpha = 200;
				}else{
					alpha = 100;
				}
			}
			if(countAlphaTime>=alphaTime){
				if (JumpRectangleActivity.soundonoff) {
					JumpRectangleActivity.soundpool.play(
							JumpRectangleActivity.SoundId_ChangeRestore, 1, 1, 5, 0,
							1);
				}
				alpha=255;
				cantObs=false;
			}
		}
		m.postRotate(this.rotationAngle, width / 2,height / 2);
		m.postTranslate(x, y);
		
		p.setAntiAlias(true);
		p.setAlpha(alpha);
		canvas.drawBitmap(bitmap, m, p);
	}
	
	public void initMe(){
		this.y=Stage.convert(120);
		x=Stage.gameViewHeight;
		countAlphaTime=0;
		cantObs=false;
		cantFloor=false;
		rotationAngle=0;
		width=Stage.convert(30);
		height=Stage.convert(30);
		alpha=255;
		this.dump();
		v=0;
		jumpcount=0;
	}

	public void setAlpha(int alpha) {
		countAlphaTime=0;
		this.cantObs=true;
		this.alpha = alpha;
	}

	public void scale(float offset){
		countScaleTime=0;
		width = Stage.convert(30) + offset * Stage.convert(10);
		height = Stage.convert(30) + offset * Stage.convert(10);
	}
	
	public void jump() {
//		Log.d("fax", "jump!");
		if (!jumping) {
			recordTimer = 0;
			jumpcount++;
			v = 0;
			recordX = x;
			walking = false;
			jumping = true;
			dumping = false;
		}
	}
	public void dump(){
//		Log.d("fax", "dump!");
		recordTimer=0;
		v=0;
		recordX=x;
		walking=false;
		jumping=false;
		dumping=true;
	}
	public void walk(){
//		Log.d("fax", "walk!");
		rotationAngle=0;
		v=0;
		recordTimer=0;
		walking=true;
		jumping=false;
		dumping=false;
		lockJumpX=Stage.gameViewHeight;
	}
	
	public void walkAt(Obj o){
		walk();
		this.x=o.x+o.height;
	}
	public void lockAt(Obj o){
		this.lockJump=true;
		this.x=o.x-this.height-Stage.convert(2);
	}
	
	public PointF getCentrePoint(){
		return new PointF(x+height/2,y+width/2);
	}
	public boolean isInside(float px,float py){
//		Log.d("fax",""+px+","+py);
		if(jumping){
			if(Math.hypot(x+height/2-px,y+width/2-py)<0.55*height){
				return true;
			}else{
				return false;
			}
		}else{
			if(px>=x&&px<=x+height&&py>=y&&py<=y+width){
				return true;
			}else{
				return false;
			}
		}
	}
	public boolean isNearMe(Obj temp){
		float y=this.y;
		float x=this.x;
		float tempx=temp.x;
		float tempy=temp.y;
		float tempw=temp.width;
		float temph=temp.height;
		if(y<=tempy+tempw&&y+width>=tempy&&x<=tempx+temph&&x+diagonal>=tempx){
			return true;
		}
		return false;
	}
	public void clearScreenObs(boolean isAddScore){
		int count =Obstacles.obstacles.size();
		for (int i=0;i<count;i++) {
			Obstacles o=Obstacles.obstacles.get(i);
			if(o.y>0&&o.y<Stage.gameViewWidth){
				if (isAddScore) {
					o.doDelCase(myview,this);
				}else{
					o.delme();
				}
				i--;
				count =Obstacles.obstacles.size();
			}
		}
	}
	public void changeScreenObsToStar(){
		int count =Obstacles.obstacles.size();
		for (int i=0;i<count;i++) {
			Obstacles o=Obstacles.obstacles.get(i);
			if(o.y>0&&o.y<Stage.gameViewWidth){
				Eatable e=new Star(0, 0);
				e.x=o.x;
				e.y=o.y;
				o.delme();
				i--;
				count =Obstacles.obstacles.size();
				Collections.sort(Eatable.eatables);
			}
		}
	}
}