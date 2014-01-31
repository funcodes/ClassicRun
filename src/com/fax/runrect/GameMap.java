package com.fax.runrect;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParser;

import com.fax.runrect.R;
import com.fax.runrect.R.xml;

import android.content.res.Resources;
import android.util.Log;

public class GameMap {
	int width;
	int height;
	int tilewidth;
	int tileheight;
	StringReader mapdata;
	static Resources r;
	static int[] maps=new int[11];
	static void initResources(Resources resources){
		r=resources;
		maps[0]=R.xml.map1;
		maps[1]=R.xml.map2;
		maps[2]=R.xml.map3;
		maps[3]=R.xml.map4;
		maps[4]=R.xml.map5;
		maps[5]=R.xml.map6;
		maps[6]=R.xml.map7;
		maps[7]=R.xml.map8;
		maps[8]=R.xml.map9;
		maps[9]=R.xml.map10;
		maps[10]=R.xml.map11;
		
	}
	static void creatNowStageMap(){
		new GameMap();
		Collections.sort(Obstacles.obstacles);
		Collections.sort(Floor.allfloors);
		Collections.sort(Eatable.eatables);
		Stage.loaded();
	}
	public GameMap(){
		try {
			this.psrserMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.initMap();
	}
	void initMap(){
//		Log.d("fax", mapdata);
		BufferedReader br=new BufferedReader(mapdata);
		int x=325;
		int theight=tileheight;
		int twidth=tilewidth;
		while(true){
			try {
				x-=theight;
				String s=br.readLine();
				if(s==null){
					break;
				}
				String[] c=s.split(",");
				for(int i=0;i<c.length;i++){
					long y=i*twidth;
					switch(Integer.valueOf(c[i])){
					case 1:	new ObsRect(x,y);break;
					case 2:	new Obs16Out(x,y);break;
					case 3:	new Obs3Out(x,y);break;
					case 4:	new ChangeAlpha(x,y);break;
					case 5:	new ChangeSmall(x,y);break;
					case 6:	new ChangeBig(x,y);break;
					case 7:	new FastRun(x,y);break;
					case 8:	new KillObs(x,y);break;
					case 9:	new ObsChangtoScore(x,y);break;
					case 10:new Star(x,y);break; 
					case 11:new Floor5(x,y);break;
					case 12:new JumpFloor(x,y);break;
					case 13:new HighJumpFloor(x,y);break;
					case 15:new WinFloor(x,y);break;
					}
				}
			} catch (Exception e) {
			}
			
		}
	}
	
	public boolean psrserMap() throws Exception {

		// 获取Pull解析器
		XmlPullParser parser =r.getXml(maps[Stage.nowStage-1]);
		// 解析器加载输入流
//		parser.setInput(in, "UTF-8");

//		List<GameMap> list = new ArrayList<GameMap>();
		for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser
				.next())
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("map".equals(parser.getName())) {
					Log.d("fax", "find map");
					String temp = parser.getAttributeValue(2);
					this.width = Integer.valueOf(temp); 
					temp = parser.getAttributeValue(3);
					this.height = Integer.valueOf(temp);
					temp = parser.getAttributeValue(4);
					this.tilewidth = Integer.valueOf(temp);
					temp = parser.getAttributeValue(5);
					this.tileheight = Integer.valueOf(temp);
//					list.add(map);
				} else if ("data".equals(parser.getName())) {
					Log.d("fax", "find data");
					String temp = parser.getAttributeValue(0);
					String temp2 = parser.getAttributeName(0);
					if (temp2.equals("encoding") && temp.equals("csv")) {
						Log.d("fax", "get mapdata");
						mapdata = new StringReader(parser.nextText());
						return true;
					}
				}
			}
		return false;
	}
	
}
class InitMapThread extends Thread{

	public void run() {
		GameMap.creatNowStageMap();
	}
	
}