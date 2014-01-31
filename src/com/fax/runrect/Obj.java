package com.fax.runrect;

import java.lang.reflect.Constructor;
import java.util.Random;

import com.fax.runrect.R;
import com.fax.runrect.R.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class Obj implements Comparable<Obj> {
	float x, y;
	float height , width ;
	float column;
	Bitmap bitmap0;
	static final int CASE_NOCASE=0;
	static final int CASE_HIT=1;
	static final int CASE_SAVE=2;
	static final int CASE_JUMP=3;
	static final int CASE_HIGHJUMP=4;
	static final int CASE_GAMEWIN=4;
	int enterUpCase=0;
	int enterLeftCase=0;
	int enterDownCase=0;

	Paint p=new Paint();
	public Obj(){
		this.x = 0;
		this.y = 0;
		this.bitmap0 = null;
	}
	public Obj(float x, float y, Bitmap bitmap) {
		this.x = Stage.convert(x);
		this.y = Stage.convert(y);
		this.bitmap0 = bitmap;
		this.column=Stage.getColumn(this.x);
		
	}

	public void drawself(Canvas canvas) {
		canvas.drawBitmap(bitmap0, x, y, null);
	}

	public void move() {
		this.y -= Stage.moveSpeed*Stage.moveSpeedScale;
	}

	public void move(int times) {
		this.y -=times* Stage.moveSpeed*Stage.moveSpeedScale;
	}
	public void delme(){
		
	}
	/**同时连续创建数个物体*/
	public void creat(int amount) {
		try {
			Constructor<? extends Obj> c=this.getClass().getConstructor(float.class,float.class);
			for (int i = 1; i < amount; i++) {
				c.newInstance(x/JumpRectangleActivity.scale,(y+width*i)/JumpRectangleActivity.scale);
			}
		} catch (Exception e) {
		}
	}
	
	public void upAt(Obj o){
		this.x=o.x+o.height;
	}
	
	public static void initBitmap(Resources r) {
		NormalFloor.bitmap = BitmapFactory.decodeResource(r,R.drawable.normalfloor);
		LineFloor.bitmap = BitmapFactory.decodeResource(r,R.drawable.linefloor);
		StaticFloor.bitmap=BitmapFactory.decodeResource(r,R.drawable.staticfloor);
		Floor5.bitmap=BitmapFactory.decodeResource(r,R.drawable.floor5);
		JumpFloor.bitmap=BitmapFactory.decodeResource(r,R.drawable.floor_j);
		HighJumpFloor.bitmap=BitmapFactory.decodeResource(r,R.drawable.floor_hj);
		ObsRect.bitmap = BitmapFactory.decodeResource(r, R.drawable.obs_rect);
		Obs3Out.bitmap = BitmapFactory.decodeResource(r, R.drawable.obs_3out);
		Obs16Out.bitmap = BitmapFactory.decodeResource(r, R.drawable.obs_16out);
		Star.bitmap = BitmapFactory.decodeResource(r, R.drawable.star);
		ObsChangtoScore.bitmap= BitmapFactory.decodeResource(r, R.drawable.obschangtoscore);
		FastRun.bitmap= BitmapFactory.decodeResource(r, R.drawable.fastrun);
		KillObs.bitmap= BitmapFactory.decodeResource(r, R.drawable.killobs);
		ChangeBig.bitmap = BitmapFactory.decodeResource(r, R.drawable.changebig);
		ChangeSmall.bitmap = BitmapFactory.decodeResource(r, R.drawable.changesmall);
		ChangeAlpha.bitmap= BitmapFactory.decodeResource(r, R.drawable.changealpha);
		Add1.bitmap= BitmapFactory.decodeResource(r, R.drawable.add1);
		Add5.bitmap= BitmapFactory.decodeResource(r, R.drawable.add5);
	}
	public PointF getCentrePoint(){
		return new PointF(x+height/2,y+width/2);
	}

	public boolean isHit(JumpRect jumpRect) {
		if (isPolygonContainPoint(jumpRect.getCentrePoint(),
				getHitArea(jumpRect))) {
			return true;
		}
		return false;
	}
	public PointF[] getHitArea(JumpRect jumpRect) {
		return null;
	}

	public boolean isSave(JumpRect jumpRect) {
		if(this.enterDownCase==Obj.CASE_SAVE){
			if(isPolygonContainPoint(jumpRect.getCentrePoint(), getDownArea(jumpRect))){
				return true;
			}
		}
		if(this.enterUpCase==Obj.CASE_SAVE){
			if(isPolygonContainPoint(jumpRect.getCentrePoint(), getUpArea(jumpRect))){
				return true;
			}
		}
		if(this.enterLeftCase==Obj.CASE_SAVE){
			if(isPolygonContainPoint(jumpRect.getCentrePoint(), getLeftArea(jumpRect))){
				return true;
			}
		}
		return false;
	}
	public boolean isInUpArea(JumpRect jumpRect) {
		if(jumpRect.x+jumpRect.height/2<this.x+this.height/2){
			return false;
		}
		if(isPolygonContainPoint(jumpRect.getCentrePoint(), getUpArea(jumpRect))){
			return true;
		}
		return false;
	}
	public boolean isInDownArea(JumpRect jumpRect) {
		if(jumpRect.x+jumpRect.height/2>this.x+this.height/2){
			return false;
		}
		if(isPolygonContainPoint(jumpRect.getCentrePoint(), getDownArea(jumpRect))){
			return true;
		}
		return false;
	}
	public boolean isInLeftArea(JumpRect jumpRect) {
		if(jumpRect.y+jumpRect.width/2>this.y+this.width/2){
			return false;
		}
		if(isPolygonContainPoint(jumpRect.getCentrePoint(), getLeftArea(jumpRect))){
			return true;
		}
		return false;
	}
	
	public PointF[] getUpArea(JumpRect jumpRect) {
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		float objMin=Math.min(height, width);
		float rectMin=Math.min(jumpRect.height, jumpRect.width);
		PointF[] pointFs = {
				new PointF(x + height + rectMin / 2, y),
				new PointF(x + height+rectMin*0.7f/2, y-rectMin*0.7f/2),
				new PointF(x + height- objMin/2, y + objMin/2),
				new PointF(x + height- objMin/2, y + width- objMin/2),
				new PointF(x + height+rectMin*0.7f/2, y + width+rectMin*0.7f/2),
				new PointF(x + height + rectMin / 2, y+ width) };
		return pointFs;
	}
	public PointF[] getDownArea(JumpRect jumpRect) {
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		float objMin=Math.min(height, width);
		float rectMin=Math.min(jumpRect.height, jumpRect.width);
		PointF[] pointFs = {
				new PointF(x-rectMin / 2, y),
				new PointF(x-rectMin*0.7f/2, y-rectMin*0.7f/2),
				new PointF(x + objMin/2, y + objMin/2),
				new PointF(x + objMin/2, y + width- objMin/2),
				new PointF(x-rectMin*0.7f/2, y + width+rectMin*0.7f/2),
				new PointF(x - rectMin / 2, y+ width) };
		return pointFs;
	}
	public PointF[] getRightArea(JumpRect jumpRect) {
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		float objMin=Math.min(height, width);
		float rectMin=Math.min(jumpRect.height, jumpRect.width);
		PointF[] pointFs = {
				new PointF(x , y +width+rectMin/2),
				new PointF(x-rectMin*0.7f/2, y + width+rectMin*0.7f/2),
				new PointF(x + objMin/2, y + width- objMin/2),
				new PointF(x +height- objMin/2, y + width- objMin/2),
				new PointF(x + height+rectMin*0.7f/2, y + width+rectMin*0.7f/2),
				new PointF(x+height , y +width+rectMin/2)};
		return pointFs;
	}

	public PointF[] getLeftArea(JumpRect jumpRect) {
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		float objMin=Math.min(height, width);
		float rectMin=Math.min(jumpRect.height, jumpRect.width);
		PointF[] pointFs = {
				new PointF(x , y -rectMin/2),
				new PointF(x-rectMin*0.7f/2, y -rectMin*0.7f/2),
				new PointF(x + objMin/2, y + objMin/2),
				new PointF(x +height- objMin/2, y + objMin/2),
				new PointF(x + height+rectMin*0.7f/2, y -rectMin*0.7f/2),
				new PointF(x+height , y -rectMin/2)};
		return pointFs;
	}

	boolean isPolygonContainPoint(PointF pointF, PointF[] vertexPointFs) {
		int nCross = 0;
		int length=vertexPointFs.length;
		for (int i = 0; i < length; i++) {
			PointF p1 = vertexPointFs[i];
			PointF p2 = vertexPointFs[(i + 1) % length];
			float p1y=p1.y;
			float p2y=p2.y;
			float p1x=p1.x;
			float pointFy=pointF.y;
			if (p1y == p2y)
				continue;
			if (pointFy >= Math.max(p1y, p2y))
				continue;
			if (pointFy < Math.min(p1y, p2y))
				continue;
			double x = (double) (pointFy - p1y) * (double) (p2.x - p1x)
					/ (double) (p2y - p1y) + p1x;
			if (x >= pointF.x)
				nCross++;
		}
		return (nCross % 2 == 1);
	}

	public void doUpCase(int caseType,JumpRect jumpRect){
		if (caseType==Obj.CASE_NOCASE){
		}else if(caseType==Obj.CASE_SAVE){
			if (jumpRect.v < 0) {
				jumpRect.walkAt(this);
			}
		}else if(caseType==Obj.CASE_HIT){
			Stage.gameOver();
		}else if(caseType==Obj.CASE_JUMP){
			jumpRect.jump();
		}
	}
	public void doDownCase(int caseType,JumpRect jumpRect){
		if (caseType==Obj.CASE_NOCASE){
		}else if(caseType==Obj.CASE_SAVE){
			if (jumpRect.v > 0) {
				jumpRect.lockAt(this);
			}
			else if(jumpRect.v <-jumpRect.v0){//防止下落速度过快，间距过大，跳过UpArea
				jumpRect.walkAt(this);
			}
		}else if(caseType==Obj.CASE_HIT){
			Stage.gameOver();
		}
	}
	public void doLeftCase(int caseType,JumpRect jumpRect){
		if (caseType==Obj.CASE_NOCASE){
		}else if(caseType==Obj.CASE_HIT){
			Stage.gameOver();
		}else if(caseType==Obj.CASE_JUMP){
			jumpRect.jump();
		}else if(caseType==Obj.CASE_GAMEWIN){
			Stage.gameWin();
		}
	}
	public boolean isFloor(){
		return false;
	}
	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = Stage.convert(height);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
//		Log.d("fax", "new FLoor creat:y="+y/JumpRectangleGame.scale+" width="+width+ " column="+column);
		this.width = Stage.convert(width);

	}

	public int compareTo(Obj another) {
//		return (int) (another.y + another.width - this.y - this.width);
		return (int) (this.y -another.y );
	}
}