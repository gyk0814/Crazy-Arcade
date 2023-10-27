import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import GaFr.GFGame;
import GaFr.GFKey;
import GaFr.GFKeyboard;
import GaFr.GFStamp;
import GaFr.GFTexture;
import GaFr.GFU;


import GaFr.*;


public class Village {

GFStamp house_red_1 = new GFStamp("assets/village/redhouse.png");
GFStamp house_blue_2 = new GFStamp("assets/village/bluehouse.png");
GFStamp house_yellow_3 = new GFStamp("assets/village/yellowhouse.png");
GFStamp tree_4 = new GFStamp("assets/village/tree.png");
GFStamp fountain_5 = new GFStamp("assets/village/fountain.png");
GFStamp red_block_11 = new GFStamp("assets/village/redblock.png");
GFStamp orange_block_12 = new GFStamp("assets/village/orangeblock.png");
GFStamp[][] bush_13 = new GFTexture("assets/village/bush.bmp",0xFFFF00FF,0x00000000).splitIntoTiles2D(5,1);
GFStamp brown_box_14 = new GFStamp("assets/village/woodbox.png");

GFStamp[][] tiles = new GFTexture("assets/village/village_tile.png").splitIntoTiles2D(7,1);
static final int GW = 15;
static final int GH = 13;
static final int CW = 40;
public int[][] map = new int[GW][GH];
int[][] tile = new int[GW][GH];

Village(){
        Scanner sc =  new Scanner(GFU.loadTextFile("src/map_info.txt"));
        int i=0;
        int n;
        while(sc.hasNext()){
            n = sc.nextInt();
            map[i%GW][i/GW] = n;
            i++;
        }
        i=0;
        sc = new Scanner(GFU.loadTextFile("src/tile_info.txt"));
        while(sc.hasNext()){
            n = sc.nextInt();
            tile[i%GW][i/GW] = n;
            i++;
        }
        sc.close();
}
GFStamp image;
GFStamp tileImg;
void load_tile(){
    for (int x = 0; x < GW; x++)
    {
      for (int y = 0; y <GH; y++)
      {
        tiles[tile[x][y]][0].moveTo(CW*x, 20+CW*y).stamp();
      }
    }
}
void load_map(){

    for (int x = 0; x < GW; x++)
    {
      for (int y = 0; y <GH; y++)
      {
        image= getGem(map[x][y],tile[x][y]);
        if(image != null) image.moveTo(CW*x, 20+CW*y).stamp();
      }
    }
}

GFStamp getGem(int map, int tile){
    if(map==1) { house_red_1.pinY = 0.3f; return house_red_1;}
    if(map==2) { house_blue_2.pinY = 0.3f; return house_blue_2;}
    if(map==3){ house_yellow_3.pinY = 0.3f; return house_yellow_3;}
    if(map==4) {tree_4.pinY=0.3f; return tree_4;}
    if(map==5) {fountain_5.pinX = 0.35f; fountain_5.pinY = 0.5f; return fountain_5;}
    if(map==11) return red_block_11;
    if(map==12) return orange_block_12;
    if(map==13) { bush_13[0][0].rescale(0.85,0.85).pinX = 0.15f; bush_13[0][0].pinY = 0.25f; return bush_13[0][0];};
    if(map==14) return brown_box_14;
    else return null;
  }
}
