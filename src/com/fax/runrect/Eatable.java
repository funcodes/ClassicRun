package com.fax.runrect;

import java.util.ArrayList;

import com.fax.runrect.R;
import com.fax.runrect.R.drawable;

import android.graphics.Bitmap;
import android.os.Message;

public class Eatable extends Obj {

	static ArrayList<Eatable> eatables = new ArrayList<Eatable>();
	static ArrayList<Eatable> deleatables = new ArrayList<Eatable>();
	static boolean canDel=false;
	public Eatable(float x, float y, Bitmap bitmap) {
		super(x, y, bitmap);

		this.width = Stage.convert(30);
		this.height = Stage.convert(30);
		
		eatables.add(this);
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
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
	}
	public void delme(){
//		canDel=true;
//		deleatables.add(this);
		eatables.remove(this);
	}
}
class Star extends Eatable{
	static Bitmap bitmap;
	int value=1;
	
	public Star(float x, float y) {
		super(x, y, bitmap);
	}
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Star, 1,
					1, 5, 0, 1);
		}
		new Add1((x+height)/JumpRectangleActivity.scale,y/JumpRectangleActivity.scale);
		myview.addscores+=value;
	}
}
class ObsChangtoScore extends Eatable{
	static Bitmap bitmap;
	
	public ObsChangtoScore(float x, float y) {
		super(x, y, bitmap);
	}
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Eat, 1,
					1, 5, 0, 1);
		}
		Message m=JumpRectangleActivity.handler.obtainMessage();
		m.arg1=R.drawable.obschangtoscore;
		m.arg2=Stage.ANIMATION_OBSTOSTAR;
		m.what=JumpRectangleActivity.MSG_ADDATTACK;
		JumpRectangleActivity.handler.sendMessage(m);
	}
}
class KillObs extends Eatable{
	static Bitmap bitmap;
	
	public KillObs(float x, float y) {
		super(x, y, bitmap);
	}
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Eat, 1,
					1, 5, 0, 1);
		}
		Message m=JumpRectangleActivity.handler.obtainMessage();
		m.arg1=R.drawable.killobs;
		m.arg2=Stage.ANIMATION_KILLOBS;
		m.what=JumpRectangleActivity.MSG_ADDATTACK;
		JumpRectangleActivity.handler.sendMessage(m);
	}
}
class FastRun extends Eatable{
	static Bitmap bitmap;
	
	public FastRun(float x, float y) {
		super(x, y, bitmap);
	}
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Eat, 1,
					1, 5, 0, 1);
		}
		Message m=JumpRectangleActivity.handler.obtainMessage();
		m.arg1=R.drawable.fastrun;
		m.arg2=Stage.ANIMATION_FASTRUN;
		m.what=JumpRectangleActivity.MSG_ADDATTACK;
		JumpRectangleActivity.handler.sendMessage(m);
	}
}
class ChangeSmall extends Eatable{
	static Bitmap bitmap;
	public ChangeSmall(float x, float y) {
		super(x, y, bitmap);
	}

	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Change,
					1, 1, 5, 0, 1);
		}
		jumpRect.scale(-0.6f);
	}
}
class ChangeBig extends Eatable{
	static Bitmap bitmap;
	public ChangeBig(float x, float y) {
		super(x, y, bitmap);
	}

	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Change,
					1, 1, 5, 0, 1);
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Star, 1,
					1, 5, 0, 1);
		}
		jumpRect.scale(0.6f);
		if (Stage.nowStage<0) {
			new Add5((x + height) / JumpRectangleActivity.scale, y
					/ JumpRectangleActivity.scale);
			myview.addscores += 5;
		}
	}
}

class ChangeAlpha extends Eatable{
	static Bitmap bitmap;
	public ChangeAlpha(float x, float y) {
		super(x, y, bitmap);
	}
	public void doHitCase(MyGameView myview,JumpRect jumpRect){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_Change,
					1, 1, 5, 0, 1);
		}
		jumpRect.setAlpha(100);
	}
}