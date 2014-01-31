package com.fax.runrect;

import java.util.Collections;

public class Stage {
//	static int allcolumn=4;
	static final float columnWidth=80*JumpRectangleActivity.scale;
	static float gameViewWidth;
	static float gameViewHeight=320*JumpRectangleActivity.scale;
//	static float gameHeight=column*columnWidth;
//	static float gameWidth;
	static final float gameBorderDown=Stage.convert(40);
//	static float gameBorderUp=(windowsHeight-gameHeight)/2+gameHeight;
	
	static boolean isPause=true;
	
	static MyGameView myview;
	
//	static int bkgdMove=0;
	static float distance;
	static int nowStage;
	static final int STAGE_ZERO=0;
	static final int STAGE_VERYVERYEASY=-3;
	static final int STAGE_VERYEASY=-4;
	static final int STAGE_EASY=-5;
	static final int STAGE_NORMAL=-6;
	static final int STAGE_HARD=-7;
	
	static int WidthToCreatObs=250;
	static float DrawRectDuration=0.2f;
	
	static float moveSpeed=Stage.convert(6);
	static float moveSpeedScale=1f;//动态调整的一个scale，固定速度用
	static final float defaultSpeed=Stage.convert(6);
	static float CustomSpeed=Stage.convert(5);

	
	static final int STAGE_CUSTOM=-10;
	static boolean CustomObsAdd=true;
	static int Custom3RectSet=1;
	
	static Floor remarkFloor;
	/*
	 * column：底部为1，依次向上
	 * isFloor:是否是地面
	 */
	static float atColumn(float column){
		column--;
		float x=(gameBorderDown+column*columnWidth)/JumpRectangleActivity.scale;
		
		return x;
		
	}
	static float getColumn(float x){
		float column=(x-gameBorderDown)/columnWidth+1;
		return column;
	}
	
	
	public static float convert(float x){
		return x*JumpRectangleActivity.scale;
	}
	
	static void pause(){
		isPause=true;
		IsShowAnimation=false;
	}
	static void pauseOrPlay(){
		isPause=!isPause;
	}
	static void play() {
		isPause=false;
		IsShowAnimation=true;
	}
	static void gameOver(){
		if (Stage.nowStage==Stage.STAGE_ZERO) {
			Stage.restart();
		}else{
			if (JumpRectangleActivity.soundonoff) {
				JumpRectangleActivity.soundpool.play(
						JumpRectangleActivity.SoundId_GameOver, 1, 1, 10, 0, 1);
			}
			pause();
			JumpRectangleActivity.handler.sendEmptyMessage(JumpRectangleActivity.MSG_GAMEOVER);
		}
	}
	static void gameWin(){
		if (JumpRectangleActivity.soundonoff) {
			JumpRectangleActivity.soundpool.play(JumpRectangleActivity.SoundId_GameWin,
					1, 1, 10, 0, 1);
		}
		pause();
		JumpRectangleActivity.handler.sendEmptyMessage(JumpRectangleActivity.MSG_NEXTSTAGE);
	}
	static void exit(){
		isPause=true;
	}

	static void clear(){
		Obstacles.obstacles.clear();
		Floor.allfloors.clear();
		Eatable.eatables.clear();
		InfoInView.infoinviews.clear();
		Stage.distance=0;
		myview.scores=0;
		myview.basescores=0;
		myview.addscores=0;
	}
	
	static void loaded(){
		JumpRectangleActivity.handler.sendEmptyMessage(JumpRectangleActivity.MSG_LOADED);
	}
	
	static void restart(){
		start(nowStage);
	}
	static void start(int stage){
		synchronized (Obstacles.obstacles) {
			Stage.pause();
			Stage.clear();
			nowStage=stage;
			myview.jumpRect.initMe();
			Stage.initStage();
			stopAnimationAndPlay();
			if(stage>0){
				Stage.pause();
			}
		}
		JumpRectangleActivity.handler.sendEmptyMessage(JumpRectangleActivity.MSG_SHOWTEXT);
	}
	
	
	static void startAnimation(int whatanimation){
		Stage.IsShowAnimation=true;
		Stage.WhatAnimation=whatanimation;
	}
	static void stopAnimationAndPlay(){
		WhatAnimation=0;
		Stage.IsShowAnimation=false;
		DrawThread.animationCount=0;
		Stage.isPause=false;

		JumpRectangleActivity.soundpool.stop(myview.drawThread.animSoundId);
		}
	static void stopAnimation(){
		WhatAnimation=0;
		Stage.IsShowAnimation=false;
		DrawThread.animationCount=0;
		}

	static boolean IsShowAnimation=false;
	static int WhatAnimation;
	static final int ANIMATION_OBSTOSTAR=1;
	static final int ANIMATION_KILLOBS=2;
	static final int ANIMATION_FASTRUN=3;
	static void setmyview(MyGameView myview){
		Stage.myview=myview;
	}
	static void addMap(){
		switch(nowStage){
		case STAGE_ZERO:
			remarkFloor=new AlphaFloor(Stage.atColumn(1),gameViewWidth/JumpRectangleActivity.scale);
			remarkFloor.setWidth(1000);
			remarkFloor.autoCreatObsZeroStage();
			break;
		case STAGE_EASY:
			remarkFloor=new AlphaFloor(Stage.atColumn(1), gameViewWidth/JumpRectangleActivity.scale);
			remarkFloor.setWidth(2000);
			
			remarkFloor.autoCreatUpFloor(true);
			remarkFloor.autoCreatObs();
			
			break;
		case STAGE_NORMAL:
			remarkFloor=new AlphaFloor(Stage.atColumn(1),gameViewWidth/JumpRectangleActivity.scale);
			remarkFloor.setWidth(2000);
			
			remarkFloor.autoCreatUpFloor(true);
			remarkFloor.autoCreatObs();
			
			break;
		case STAGE_HARD:
			remarkFloor=new AlphaFloor(Stage.atColumn(1), gameViewWidth/JumpRectangleActivity.scale);
			remarkFloor.setWidth(2000);
			
			remarkFloor.autoCreatUpFloor(true);
			remarkFloor.autoCreatObs();
			
			break;
		case STAGE_CUSTOM:
			remarkFloor=new AlphaFloor(Stage.atColumn(1), gameViewWidth/JumpRectangleActivity.scale);
			remarkFloor.setWidth(2000);
			
			remarkFloor.autoCreatUpFloor(true);
			remarkFloor.autoCreatObs();
			
			break;
		}
		Collections.sort(Obstacles.obstacles);
		Collections.sort(Floor.allfloors);
		Collections.sort(Eatable.eatables);
	}
	static void initStage(){
		initStageSpeed();
		switch(Stage.nowStage){
		case STAGE_ZERO:
			new StaticFloor(0,0);
			addMap();
			break;
		case STAGE_EASY:
			WidthToCreatObs=300;
			new StaticFloor(0,0);
			addMap();
			break;
		case STAGE_NORMAL:
			WidthToCreatObs=250;
			new StaticFloor(0,0);
			addMap();
			break;
		case STAGE_HARD:
			WidthToCreatObs=200;
			new StaticFloor(0,0);
			addMap();
			break;
		case STAGE_CUSTOM:
			new StaticFloor(0,0);
			addMap();
			break;
		default:
			new StaticFloor(0,0);
			new InitMapThread().start();
			break;
		}
		Collections.sort(Obstacles.obstacles);
		Collections.sort(Floor.allfloors);
		Collections.sort(Eatable.eatables);
	}
	static void initStageSpeed(){
		switch(Stage.nowStage){
		case STAGE_ZERO:
			Stage.moveSpeed=Stage.defaultSpeed;
			break;
		case STAGE_EASY:
			Stage.moveSpeed=Stage.convert(3);
			break;
		case STAGE_NORMAL:
			Stage.moveSpeed=Stage.convert(4);
			break;
		case STAGE_HARD:
			Stage.moveSpeed=Stage.convert(5);
			break;
		case STAGE_CUSTOM:
			Stage.moveSpeed=CustomSpeed;
			break;
		default:Stage.moveSpeed=Stage.convert(5);
			break;
		}
	}

}
