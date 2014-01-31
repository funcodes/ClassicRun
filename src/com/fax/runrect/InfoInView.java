package com.fax.runrect;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class InfoInView extends Obj {

	static ArrayList<InfoInView> infoinviews = new ArrayList<InfoInView>();
	static ArrayList<InfoInView> delinfoinviews = new ArrayList<InfoInView>();
	static boolean canDel=false;
	
	int alpha=255;
	public InfoInView(float x, float y, Bitmap bitmap) {
		super(x, y, bitmap);

		this.width = Stage.convert(30);
		this.height = Stage.convert(30);
		
		infoinviews.add(this);
	}
	public void drawself(Canvas canvas) {
		alpha-=10;
		if(alpha<=50){
			this.delme();
		}
		
		p.setAntiAlias(true);
		p.setAlpha(alpha);
		canvas.drawBitmap(bitmap0, x,y, p);
	}
	public void delme(){
//		canDel=true;
//		delinfoinviews.add(this);
		infoinviews.remove(this);
	}
}

class Add1 extends InfoInView{
	static Bitmap bitmap;
	public Add1(float x, float y) {
		super(x, y, bitmap);
	}
	public void move() {
		if(this.alpha<=220){
			this.x+=Stage.convert(6);
		}
	}
	
}
class Add5 extends InfoInView{
	static Bitmap bitmap;
	public Add5(float x, float y) {
		super(x, y, bitmap);
	}
	public void move() {
		if(this.alpha<=220){
			this.x+=Stage.convert(6);
		}
	}
	
}