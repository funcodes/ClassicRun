package com.fax.runrect;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Floor extends Obj {

	static ArrayList<Floor> allfloors = new ArrayList<Floor>();
	static ArrayList<Floor> delfloors = new ArrayList<Floor>();
	static boolean canDel=false;
	public Floor(float x, float y, Bitmap bitmap) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(10);

		this.enterDownCase = Obj.CASE_SAVE;
		this.enterUpCase = Obj.CASE_SAVE;

		allfloors.add(this);
	}

	public boolean isFloor() {
		return true;
	}

	public void delme(){
//		canDel=true;
//		delfloors.add(this);
		allfloors.remove(this);
	}

	/**在floor上自动创建障碍物（起始页）*/
	public void autoCreatObsZeroStage(){
		float x=this.x;
		float y=this.y;
		float width=this.width;
		float height=this.height;
		float scale=JumpRectangleActivity.scale;
		
		int everyWidth=200;//每批障碍物的占用floor长度
		
		Random r = new Random();
		
		float creatMaxLong=Stage.convert(120);//这批障碍物最长长度
		int count = (int) (width/scale/ everyWidth);
		int i = 0;
		while ( i < count) {
			int offset=60+ r.nextInt(30);//每批障碍物之间的间隔偏移
			Obj o;
				
			int n=r.nextInt(3);
			if (n==2) {
				o = new ObsRect((x + height) /scale, y/scale+ offset + i * everyWidth );
			}else if(n==1){
				o = new Obs3Out((x + height) /scale, y/scale  + offset+ i * everyWidth);
			}else{
				o = new Obs16Out((x + height) /scale, y/scale + offset + i * everyWidth);
			}
			o.upAt(this);
			float owidth=o.width;
			int c=r.nextInt((int)(creatMaxLong/owidth))+1;
			o.creat(c);
			
			//创建自动跳跃的floor
			AlphaJumpFloor jf=new AlphaJumpFloor(o.x/scale, (o.y-owidth)/scale);
			if(c==1){
				jf.y-=(r.nextInt(3)+1)*jf.width*0.8f;
			}
			if(c==2){
				jf.y-=(r.nextInt(3))*jf.width*0.8f;
			}
			jf.width=4*jf.width;
			i++;
		}
	}

	/**在floor上自动创建障碍物（休闲模式）*/
	public void autoCreatObs() {
		float x=this.x;
		float y=this.y;
		float width=this.width;
		float height=this.height;
		float scale=JumpRectangleActivity.scale;
		
		int everyWidth=Stage.WidthToCreatObs;
		
		Random r = new Random();
		
		float creatMaxLong=Stage.convert(90);
		int count = (int) (width/scale/ everyWidth);
		int i = 0;
		while ( i < count) {
			float offset=60+ r.nextInt((int)(everyWidth/4));
				Obj o=null;
				int n=r.nextInt(4);
				if (n==2) {
					o = new ObsRect((x + height) /scale, y/scale + i * everyWidth + offset);
				}else if(n==1){
					o = new Obs3Out((x + height) /scale, y/scale + i * everyWidth + offset);
				}else if(n==0){
					o = new Obs16Out((x + height) /scale, y/scale + i * everyWidth + offset);
				}else if(n==3){//创建eatable
					int n2=r.nextInt(7);
					if (n2==0){
						o = new ChangeBig((x + height) /scale, y/scale + i * everyWidth + offset);
					}else if (n2==1){
						o = new ChangeSmall((x + height) /scale, y/scale + i * everyWidth + offset);
					}else if (n2==2&&r.nextInt(3)==0){
						o = new ChangeAlpha((x + height) /scale, y/scale + i * everyWidth + offset);
					}else if (n2==3&&r.nextInt(3)==0){
						o = new ObsChangtoScore((x + height) /scale, y/scale + i * everyWidth + offset);
					}else if (n2==4&&r.nextInt(4)==0){
						o = new KillObs((x + height) /scale, y/scale + i * everyWidth + offset);
					}else if (n2==5&&r.nextInt(2)==0){
						o = new FastRun((x + height) /scale, y/scale + i * everyWidth + offset);
					}else{
						o = new Star((x + height) /scale, y/scale + i* everyWidth + offset);
					}
				}
				o.upAt(this);
				int c=r.nextInt((int)(creatMaxLong/o.width))+1;
				int nowStage=Stage.nowStage;
				if (nowStage == Stage.STAGE_EASY) {
					if (c==3&&o.getClass().equals(ObsRect.class)) {
						c=2;
					}
				}
				if (nowStage == Stage.STAGE_NORMAL) {
					if (c==3&&o.getClass().equals(ObsRect.class)) {
						if (r.nextInt(2)==0) {
							c = 2;
						}
					}
				}
				if(nowStage==Stage.STAGE_HARD){
					c++;
					if (c==4) {
						c=3;
					}
				}
				if(nowStage==Stage.STAGE_CUSTOM){
					if (Stage.CustomObsAdd) {
						c++;
						if (c == 4) {
							c = 3;
						}
					}
					if (Stage.Custom3RectSet==2) {
						if (c == 3 && o.getClass().equals(ObsRect.class)) {
							if (r.nextInt(2) == 0) {
								c = 2;
							}
						}
					}else if (Stage.Custom3RectSet==0) {
						if (c==3&&o.getClass().equals(ObsRect.class)) {
							c=2;
						}
					}
				}
				if(y+width-(o.y+o.width)<Stage.convert(120)){
					c=1;
				}
				if (o.getClass().getSuperclass().equals(Eatable.class)) {
					if(!o.getClass().equals(Star.class)){
						c=1;
					}
				}
				o.creat(c);
			i++;
		}
	}

	/**在floor上自动创建第二层floor*/
	public void autoCreatUpFloor(boolean ifCreatObs) {
		float x=this.x;
		float y=this.y;
		float width=this.width;
		float height=this.height;
		float scale=JumpRectangleActivity.scale;
		
		Random r = new Random();
		int everyWidth=0;
		float lastY=y/scale;
		while (true) {
			everyWidth=(int)(width/scale/2);
			if(everyWidth>1000){
				everyWidth=1000;
			}
			everyWidth=(int)(everyWidth*(r.nextInt(7)+6)/12);
			float offset =everyWidth/4 + r.nextInt(everyWidth);
			if(lastY+offset+everyWidth>(y+width)/scale){
				break;
			}
//			if(lastY+offset<300){//防止游戏一开始就有Floor
//				continue;
//			}
			Floor subObj;
			if (r.nextInt(2)==0) {
				subObj = new Floor5(0, offset+ lastY);
			}else if (r.nextInt(2)==0) {
				subObj = new Floor5(0, offset+ lastY);
			}else{
				subObj = new LineFloor(0,offset+ lastY);
			}
			subObj.x=x+height+Stage.columnWidth;
			subObj.setWidth(everyWidth);
			lastY=(subObj.y+subObj.width)/scale;
			
			if(ifCreatObs){
				subObj.autoCreatObs();
			}
			if(subObj.x<Stage.atColumn(3)*scale){
				subObj.autoCreatUpFloor(ifCreatObs);
			}
		}
	}
}

class NormalFloor extends Floor {
	static Bitmap bitmap;
	NinePatch np;

	public NormalFloor(float x, float y) {
		super(x, y, bitmap);
//		this.enterRightCase = Obj.CASE_HIT;
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}

class LineFloor extends Floor {
	static Bitmap bitmap;
	NinePatch np;

	public LineFloor(float x, float y) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(3);
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}
class AlphaFloor extends Floor {

	public AlphaFloor(float x, float y) {
		super(x, y, null);
		this.width = Stage.convert(30);
		this.height = Stage.convert(0);

		this.enterDownCase = Obj.CASE_NOCASE;
		this.enterUpCase = Obj.CASE_NOCASE;
	}

	public void drawself(Canvas canvas) {
		
	}
}

class AlphaJumpFloor extends Floor {
	public AlphaJumpFloor(float x, float y) {
		super(x, y, null);
		this.width = Stage.convert(30);
		this.height = Stage.convert(5);
		
		this.enterUpCase = Obj.CASE_JUMP;
		this.enterLeftCase = Obj.CASE_JUMP;
	}

	public void drawself(Canvas canvas) {
	}
}
class JumpFloor extends Floor {
	static Bitmap bitmap;
	NinePatch np;
	public JumpFloor(float x, float y) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(5);
		
		this.enterUpCase = Obj.CASE_JUMP;
		this.enterLeftCase = Obj.CASE_JUMP;
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}
class HighJumpFloor extends Floor {
	static Bitmap bitmap;
	NinePatch np;
	public HighJumpFloor(float x, float y) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(5);
		
		this.enterUpCase = Obj.CASE_HIGHJUMP;
		this.enterLeftCase = Obj.CASE_HIGHJUMP;
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}
class StaticFloor extends Floor {
	static Bitmap bitmap;

	public StaticFloor(float x, float y) {
		super(x, y, bitmap);
		this.width=Stage.gameViewWidth+Stage.convert(40);
		this.height=Stage.gameBorderDown;
	}

	@Override
	public void move() {
		super.move();
		while(this.y<=-Stage.convert(40)){
			this.y+=Stage.convert(40);
		}
	}
}

class Floor5 extends Floor{
	static Bitmap bitmap;
	NinePatch np;

	public Floor5(float x, float y) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(5);
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}
class Floor6 extends Floor{
	NinePatch np;

	public Floor6(float x, float y) {
		super(x, y, Floor5.bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(6);
		np = new NinePatch(bitmap0, bitmap0.getNinePatchChunk(), null);
	}

	public void drawself(Canvas canvas) {
		np.draw(canvas, new RectF(x, y, x + getHeight(), y + getWidth()));
	}
}
class WinFloor extends Floor{
	Paint paint;
	Paint linepaint;
	public WinFloor(float x, float y) {
		super(x, y, null);
		this.width = Stage.convert(5);
		this.height = Stage.gameViewHeight-this.x;
		this.enterLeftCase = Obj.CASE_GAMEWIN;
		paint=new Paint();
		linepaint=new Paint();
		paint.setColor(Color.WHITE);
		linepaint.setColor(Color.RED);
	}

	public void drawself(Canvas canvas) {
		canvas.drawRect(x, y, x+height, y+width, paint);
		canvas.drawLine(x, y, x+height, y, linepaint);
		canvas.drawLine(x, y+width, x+height, y+width, linepaint);
	}
}