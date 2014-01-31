package com.fax.runrect;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class Obstacles extends Obj {
	static ArrayList<Obstacles> obstacles = new ArrayList<Obstacles>();
	static ArrayList<Obstacles> delobstacles = new ArrayList<Obstacles>();
	static boolean canDel=false;
	public Obstacles(float x, float y, Bitmap bitmap) {
		super(x, y, bitmap);
		this.width = Stage.convert(30);
		this.height = Stage.convert(30);

		this.enterDownCase = Obj.CASE_HIT;
		this.enterUpCase = Obj.CASE_HIT;
		this.enterLeftCase = Obj.CASE_HIT;

		obstacles.add(this);
	}

	public boolean isFloor() {
		return false;
	}
	public void doDelCase(MyGameView myview,JumpRect jumpRect){
		myview.addscores++;
		new Add1((x+height)/JumpRectangleActivity.scale,y/JumpRectangleActivity.scale);
		this.delme();
	}
	public void delme(){
//		canDel=true;
//		delobstacles.add(this);
		obstacles.remove(this);
	}
}
//矩形障碍物
class ObsRect extends Obstacles {
	static Bitmap bitmap;

	public ObsRect(float x, float y) {
		super(x, y, bitmap);
	}
	public PointF[] getHitArea(JumpRect jumpRect) {
		float rectMin = Math.min(jumpRect.height, jumpRect.width);
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		PointF[] pointFs = { new PointF(x + height + rectMin / 2, y),
				new PointF(x + height + rectMin / 2, y+ width),
				new PointF(x+height , y +width+rectMin/2),
				new PointF(x , y +width+rectMin/2),
				new PointF(x - rectMin / 2, y+ width),
			 	new PointF(x - rectMin / 2, y),
				new PointF(x , y -rectMin/2),
				new PointF(x+height , y -rectMin/2)};
		return pointFs;
	}
}

//球刺
class Obs16Out extends Obstacles {
	static Bitmap bitmap;

	public Obs16Out(float x, float y) {
		super(x, y, bitmap);
	}

	public boolean isHit(JumpRect jumpRect) {
		float rectMin2 = Math.min(jumpRect.height, jumpRect.width)/2;
		float r2=height/2;
		float oX=getCentrePoint().x;
		float oY=getCentrePoint().y;
		float jX=jumpRect.getCentrePoint().x;
		float jY=jumpRect.getCentrePoint().y;
		if ((oX-jX)*(oX-jX)+ (oY-jY)*(oY-jY)< (r2+rectMin2)* (r2+rectMin2)) {
			return true;
		}
		return false;
	}
}
//三角形
class Obs3Out extends Obstacles {
	static Bitmap bitmap;

	public Obs3Out(float x, float y) {
		super(x, y, bitmap);
	}

	public PointF[] getHitArea(JumpRect jumpRect) {
		float x=this.x;
		float y=this.y;
		float height=this.height;
		float width=this.width;
		float rectMin = Math.min(jumpRect.height, jumpRect.width);
		PointF[] pointFs = { new PointF(x, y - rectMin / 2),
				new PointF(x+rectMin / 2, y - rectMin / 2),
				new PointF(x + height + rectMin / 2, y + width / 2),
				new PointF(x+rectMin / 2, y+ width + rectMin / 2),
				new PointF(x, y + width + rectMin / 2) };
		return pointFs;
	}
}