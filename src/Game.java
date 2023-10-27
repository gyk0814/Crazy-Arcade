import GaFr.GFGame;
import GaFr.GFKey;
import GaFr.GFKeyboard;
import GaFr.GFStamp;
import GaFr.GFTexture;
import GaFr.*;

import java.util.ArrayList;


import GaFr.GFFont;
import GaFr.Gfx;
import GaFr.Gfx.Color;
import GaFr.GFU;

 class Bomb
{
  static final int CW = 40;
  ArrayList<GFStamp> bubble_frames;
  int x=0,y=0,character;
  int timer=180;
  int ticks = 0;
  float p;


  Bomb(int x,int y, int c, ArrayList<GFStamp> bubble_frames){
    this.x = x;
    this.y = y;
    this.character = c;
    this.bubble_frames = bubble_frames;
  }
  boolean isBazzi(){
    return character==1 ? true : false;
  }
  void countPop(){
    timer--;
  }
  boolean moveAcross(){
    return timer>120 ? true : false;
  }

  boolean isPop(){
    return (timer<0) ? true : false;
  }

  void draw(){
    if(!isPop()){
      ticks++;
      p = ticks/(float)75;
      ticks %= 80;
      int which = (int)(p * bubble_frames.size());
      if(which >= bubble_frames.size()) which = bubble_frames.size() - 1;
      GFStamp bomb = bubble_frames.get(which);
      bomb.moveTo(x*CW,y*CW+20).stamp();
    }
  }
}

class Alpha{
  double val = 0.5;
  double opaque(){
    val+=0.001;
    return val;
  }
}

public class Game extends GFGame
{
  {
    Gfx.clearColor(Gfx.Color.BLACK);
  }
  static final int NOTHING = 0;
  static final int BOMB_VAL = -8;
  static final int BOMB_POP = -10;
  // static final int BUSH_BOMB = -9;
  static final int EXPLODABLE_BOX = -1;
  static final int NON_EXPLODABLE_BOX = -2;
  static final int BOMB_GO_ACROSS =8;
  static final int CW = 40; //cell width
  static final int GW = 15;
  static final int GH = 13;
  // static final int NOTHING = 0;
  // static final int BOMB_VAL = -8;
  // static final int BOMB_POP = -10;
  // static final int EXPLODABLE_BOX = -1;
  // static final int NON_EXPLODABLE_BOX = -2;
  // static final int BOMB_GO_ACROSS =8;
  // static final int CW = 38; //cell width
  // static final int GW = 15;
  // static final int GH = 13;

  GFSound bgm = new GFSound("assets/bgm.mp3");
  GFSound explosion = new GFSound("assets/explosion.mp3");

  GFFont font_big = new GFFont("gafr/fonts/spleen/spleen-16x32.ffont.json");
  GFFont font_med = new GFFont("gafr/fonts/spleen/spleen-8x16.ffont.json");
  GFFont font_small = new GFFont("gafr/fonts/spleen/spleen-6x12.ffont.json");

  GFStamp[][] gemImages = new GFTexture("assets/gamemap3.png").splitIntoTiles2D(15, 15);
  double gemH = gemImages[0][0].height;
  double gemW = gemImages[0][0].width;

  GFStamp cookie1 = new GFStamp("assets/cookie.png").resize(gemH,gemW+3);
  GFStamp cookie2 = new GFStamp("assets/cookie2.png").resize(gemH,gemW+3);
  // GFStamp bomb = new GFStamp("assets/bomb.png").resize(gemH,gemW);
  GFStamp bigBomb = new GFStamp("assets/bomb.png").rescale(1.8,1.8);
  GFStamp trapped = new GFStamp("assets/trapped.png").rescale(0.5,0.5);
  {trapped.pinX = 0.5f;
  trapped.pinY =0.5f;
  }


  GFStamp[] bombPop = new GFStamp[4];
  GFStamp popL = new GFStamp("assets/splashLeftEnd.png").resize(gemH,gemW);
  GFStamp popR = new GFStamp("assets/splashRightEnd.png").resize(gemH,gemW);
  GFStamp popU = new GFStamp("assets/splashTopEnd.png").resize(gemH,gemW);
  GFStamp popD = new GFStamp("assets/splashBottomEnd.png").resize(gemH,gemW);
  GFStamp popC = new GFStamp("assets/splashCenter.png").resize(gemH,gemW);

  //bazzi properties
  int bazzi_pos = 0;
  int bazzi_x = 20;
  int bazzi_y = 20;
  boolean bazzi_Hit = false;
  boolean bazzi_end = false;

  //woonie properties
  int woonie_pos = 0;
  int woonie_x = CW*GW-25;
  int woonie_y = CW*GH-75;
  boolean woonie_Hit = false;
  boolean woonie_end = false;

  //winner properties
  int winner_x=0;
  int winner_y=0;
  boolean winnerJump = true;

  //game and bomb properties
  boolean gameStart = true;
  // int grid[][] = new int[15][15];

  ArrayList<Bomb> bombs = new ArrayList<>();
  int bazzi_bomb_cnt = 0;
  int woonie_bomb_cnt = 0;
  Alpha bazzi_alpha = new Alpha();
  Alpha woonie_alpha = new Alpha();

  //
  GFStamp[][] bazzi_move = new GFTexture("assets/bazzi_copy.png").splitIntoTiles2D(5,4);
  GFStamp[][] woonie_move = new GFTexture("assets/woonie.png").splitIntoTiles2D(5,4);
  GFStamp[][] bazzi_start = new GFTexture("assets/bazzi_start.bmp",0xFFFF00FF,0x00000000).splitIntoTiles2D(10,1);
  GFStamp[][] bazzi_trap = new GFTexture("assets/bazzi_trap.bmp",0xFFFF00FF,0x00000000).splitIntoTiles2D(13,1);
  GFStamp[][] bazzi_die = new GFTexture("assets/bazzi_die.bmp",0xFFFF00FF,0x00000000).splitIntoTiles2D(13,1);
  Village villageMap = new Village();
  int grid[][] = villageMap.map;

{
  bgm.play();
  explosion.volume(0.5f);
  for(int i=0;i<bazzi_move.length;i++){
    for(int j=0;j<bazzi_move[0].length;j++){
      bazzi_move[i][j].centerPin();
      woonie_move[i][j].centerPin();
    }
  }
  // grid = villageMap.map;
  // for(int i=0;i<5;i++){
  //   bazzi_move[i][3].pinY = 0.65f;
  //   bazzi_move[i][2].pinY = 0.65f;
  //   bazzi_move[i][1].pinY = 0.6f;
  // }
  bombPop[0] = popC;
  bombPop[1] = popU;
  bombPop[2] = popL;
  bombPop[3] = popR;
  bombPop[4] = popD;

  bigBomb.centerPin();

}
  int tick=0;
  int bazzi_tick=0;
  int woonie_tick=0;
  void drawCharacters(int frameCount){
    //when game starts, do this animation
    if(gameStart){
      bazzi_tick++;
      float bp = bazzi_tick/(float)60;
      int which_b = (int)(bp*bazzi_start.length);
      if(which_b >= bazzi_start.length-1){
        if(which_b%2 == 1)
          which_b = bazzi_start.length-1;
        else which_b = bazzi_start.length-2;
      }
        bazzi_start[which_b][0].rescale(1.1,1.1).centerPin().moveTo(bazzi_x,bazzi_y).stamp();
        if(bazzi_tick % 120 == 0 ) {gameStart = false; bazzi_tick =0;}
    }else {
      if(!bazzi_end){
        // bazzi[bazzi_pos].moveTo(bazzi_x,bazzi_y).stamp();
        bazzi_tick++;
        float bp = bazzi_tick/(float)60;
        int which_b = (int)(bp * bazzi_move.length) % bazzi_move.length;
        if(which_b <= bazzi_move.length ){
          if(GFKeyboard.isDown(GFKey.ArrowDown)||GFKeyboard.isDown(GFKey.ArrowUp)||GFKeyboard.isDown(GFKey.ArrowLeft)||GFKeyboard.isDown(GFKey.ArrowRight))
            bazzi_move[which_b][bazzi_pos].moveTo(bazzi_x,bazzi_y).stamp();
          else {bazzi_tick = 0; bazzi_move[2][bazzi_pos].moveTo(bazzi_x,bazzi_y).stamp();}
        }
      }
      else if(gameEnd()){
        tick++;
        float p = tick/(float)120;
        int which = (int)(p * bazzi_die.length);
        if(which <= bazzi_die.length ){
          bazzi_die[which][0].centerPin().rescale(0.9,0.9).moveTo(bazzi_x,bazzi_y).stamp();
          // bomb.moveTo(x*CW,y*CW).stamp();
        }
      }
      if(!woonie_end){
        // woonie[woonie_pos].moveTo(woonie_x,woonie_y).stamp();
        woonie_tick++;
      float wp = woonie_tick/(float)60;
      int which_w = (int)(wp*woonie_move.length) % woonie_move.length;
      if(GFKeyboard.isDown(GFKey.S)||GFKeyboard.isDown(GFKey.E)||GFKeyboard.isDown(GFKey.D)||GFKeyboard.isDown(GFKey.F)){
        woonie_move[which_w][woonie_pos].moveTo(woonie_x,woonie_y).stamp();
      }else { woonie_tick = 0; woonie_move[2][woonie_pos].moveTo(woonie_x, woonie_y).stamp();}
      }
    }
    if(gameEnd()) {
      bazzi_pos = 0;
      woonie_pos = 0;
      if(winnerJump){
        // bazzi_y -= 2;
        woonie_y -= 2;
        if(!bazzi_end && winner_y-bazzi_y >= CW )  winnerJump = false;
        if(!woonie_end && winner_y-woonie_y >= CW )  winnerJump = false;
      }else{
        // bazzi_y += 1;
        woonie_y += 1;
        if(!bazzi_end  && winner_y <=bazzi_y) winnerJump = true;
        if(!woonie_end && winner_y <=woonie_y) winnerJump = true;

      }
    }
  }
  boolean isExplodable(int val){
    // if(val>10) return true;
    if(val>10 &&val<15) return true;
    else return false;
  }

  boolean gameEnd(){
    return (bazzi_end || woonie_end);
  }

  public void removeBomb( int frameCount){
    if(!bombs.isEmpty()){
      for(int i=0;i<bombs.size();i++){
        Bomb b = bombs.get(i);
        b.countPop();

        if(!b.moveAcross() && grid[b.x][b.y]!= 13)
          setGrid(b.x, b.y, BOMB_VAL);

        if(b.isPop()){
          bombs.remove(b);
          explosion.play();
          setGrid(b.x, b.y, BOMB_POP);
          isHit(b.x, b.y);
          if(b.isBazzi()) bazzi_bomb_cnt--;
          else woonie_bomb_cnt--;
        }
      }
    }
  }
  public void removeBomb(int x, int y){
    for(Bomb b : bombs){
      if(b.x ==x &&b.y == y){
        b.timer = 0;
      }
    }
  }
  void setGrid(int x, int y, int val){
    if(x<GH && y<GW && x>=0 && y>=0){
      grid[x][y] = val;
    }
  }
  int getGrid(int x, int y){
    if(x<GH && y<GW && x>=0 && y>=0){
      return grid[x][y];
    }else return NOTHING;
  }

  void popOpponent(){
    if(bazzi_Hit || woonie_Hit){
      if((((5+woonie_x)/CW) == ((5+bazzi_x)/CW))&&((woonie_y/CW)==(bazzi_y/CW))){
        if(bazzi_Hit && !woonie_Hit)  bazzi_alpha.val = .998;
        if(woonie_Hit && !bazzi_Hit)  woonie_alpha.val = .998;
      }
    }
  }


  void isHit(int x, int y){
    int bazzi_gridX = (bazzi_x)/CW;
    int bazzi_gridY = (bazzi_y-5)/CW;
    int woonie_gridX = (woonie_x)/CW;
    int woonie_gridY = (woonie_y-5)/CW;

    if((bazzi_gridX<=(x+1) && bazzi_gridX>=x-1 && bazzi_gridY == y)||(bazzi_gridY<=(y+1) && bazzi_gridY>=y-1 && bazzi_gridX == x)){
      bazzi_Hit = true;
    }
    if((woonie_gridX<=(x+1) && woonie_gridX>=x-1 && woonie_gridY == y)||(woonie_gridY<=(y+1) && woonie_gridY>=y-1 && woonie_gridX == x)){
      woonie_Hit = true;
    }
  }
  int btrap_tick=0;
  void drawHit(int frameCount){
    if(bazzi_Hit){
      if(this.bazzi_alpha.val<1){
      //   bigBomb.moveTo(bazzi_x, bazzi_y).setAlpha(this.bazzi_alpha.opaque()).stamp();
      //   // trapped.moveTo(bazzi_x, bazzi_y).stamp();
      //   if(frameCount % 10==0){
      //     if(bazzi_pos == 0)  bazzi_pos=1;
      //     else bazzi_pos = 0;
      //   }
      btrap_tick++;
      float bp = btrap_tick/(float)220;
      int which_b = (int)(bp*bazzi_trap.length);
      if(which_b >= bazzi_trap.length-1){
        if(which_b%2 == 0) which_b = bazzi_trap.length-2;
        else which_b = bazzi_trap.length-1;
      }
      bazzi_trap[which_b][0].centerPin().moveTo(bazzi_x,bazzi_y).stamp();
      if(btrap_tick % 220 == 0 ) bazzi_alpha.val = 1;
    }else {
      bazzi_end = true;
      winner_y = woonie_y;
    }
  }
    if(woonie_Hit){
      if(this.woonie_alpha.val<1){
        bigBomb.moveTo(woonie_x, woonie_y).setAlpha(this.woonie_alpha.opaque()).stamp();
        if(frameCount % 10==0){
          if(woonie_pos == 0)  woonie_pos=1;
          else woonie_pos = 0;
        }
      }else {woonie_end = true; winner_y = bazzi_y;}
    }
  }

  public void drawPop(int frameCount){
    for(int x=0;x<GW;x++){
      for(int y=0;y<GH;y++){
        if(getGrid(x, y)== BOMB_POP){
          popC.moveTo(x*CW,y*CW+20).stamp();
          if(getGrid(x, y+1)==0) popD.moveTo(x*CW,(y+1)*CW+20).stamp(); 
          else if(getGrid(x, y+1)==BOMB_VAL) { removeBomb(x,y+1); setGrid(x,y+1,BOMB_POP);}
          // // else if(getGrid(x, y+1)==EXPLODABLE_BOX) setGrid(x,y+1,0);
          else if(isExplodable(getGrid(x,y+1))) setGrid(x,y+1,0);

          if(getGrid(x, y-1)==0) popU.moveTo(x*CW,(y-1)*CW+20).stamp();
          else if(getGrid(x, y-1)==BOMB_VAL) { removeBomb(x,y-1); setGrid(x,y-1,BOMB_POP);}
          // // else if(getGrid(x, y-1)==EXPLODABLE_BOX) setGrid(x,y-1,0);
          else if(isExplodable(getGrid(x,y-1))) setGrid(x,y-1,0);

          if(getGrid(x-1, y)==0) popL.moveTo((x-1)*CW+3,y*CW+20).stamp(); 
          else if(getGrid(x-1, y)==BOMB_VAL) { removeBomb(x-1,y); setGrid(x-1,y,BOMB_POP);}
          // // else if(getGrid(x-1, y)==EXPLODABLE_BOX) setGrid(x-1,y,0);
          else if(isExplodable(getGrid(x-1,y))) setGrid(x-1,y,0);

          if(getGrid(x+1, y)==0) popR.moveTo(2+(x+1)*CW,y*CW+20).stamp();
          else if(getGrid(x+1, y)==BOMB_VAL) { removeBomb(x+1,y); setGrid(x+1,y,BOMB_POP);}
          // // else if(getGrid(x+1, y)==EXPLODABLE_BOX) setGrid(x+1,y,0);
          else if(isExplodable(getGrid(x+1,y))) setGrid(x+1,y,0);

          if(frameCount % 60==0)
            grid[x][y]=0;
        }
      }
    }
  }

  public void createBomb(int x, int y, int character){
    // if(grid[x][y]>=0 && grid[x][y]!=BOMB_GO_ACROSS){
    if(grid[x][y]==0 || grid[x][y]==13){
      //1 = bazzi
      if(character==1 && bazzi_bomb_cnt<5){
        Bomb b = new Bomb(x, y, character, bubble_frames);
        bombs.add(b);
        bazzi_bomb_cnt++;
        // grid[x][y] = BOMB_GO_ACROSS;
        //0 = wonnie
      }else if(character==0 && woonie_bomb_cnt<5){
        Bomb b = new Bomb(x, y, character, bubble_frames);
        bombs.add(b);
        woonie_bomb_cnt++;
        // grid[x][y] = BOMB_GO_ACROSS;
      }
      if(grid[x][y]!=13) grid[x][y] = BOMB_GO_ACROSS;
    }
  }


  boolean canMove(int x, int y){
    if(x<10 || y<20 || y>485 || x>580) return false;
    System.out.println("x: "+x+" y: "+y);
    int gridVal = grid[(x-15)/CW][(y-20+25)/CW];
    int gridVal2 = grid[(x+15)/CW][(y-20)/CW];
    System.out.println("val1: "+gridVal+" val2: "+gridVal2);
    if((gridVal!=0 && gridVal!=13 &&gridVal!=BOMB_GO_ACROSS) || (gridVal2!=0 && gridVal2!=13 &&gridVal2!=BOMB_GO_ACROSS))
      return false;
    else return true;
  }

  @Override
  public void onKeyDown (String key, int code, int flags)
  {
    int x,y;
    if(!gameEnd()){
      switch (code)
      {
        case GFKey.Space:
        {
          x = (bazzi_x)/CW;
          y = (bazzi_y-20)/CW;
          createBomb(x,y,1);
          break;
        }
        case GFKey.ShiftLeft:
        {
          x = (woonie_x)/CW;
          y= (woonie_y-20)/CW;
          createBomb(x,y,0);
          break;
        }
      }
    }
  }
  void drawInstruction(){
    font_med.draw(580,20,"Bazzi: Top Left corner\nWoonie: Bottom right corner");
    font_med.draw(600,90,"KEYS: ");
    font_small.draw(600,110,"\n Bazzi - Move: arrow keys, \n         Bomb: space key\n\n Woonie- Move: E S D F, \n         Bomb: left shift key");
    font_med.draw(580,230,"HOW TO PLAY: ");
    font_small.draw(580,260,"1. Bombs go off 3 seconds later \n2. Adjacent bombs go off together\n3. you can only step on new bombs \n  for 1 second\n(if you stay, you will be trapped)\n4. White boxes are not breakable\n5. Each can put upto 5 bombs at a time\n6. when the opponent is trapped \n   inside waterbomb, you can run \n   over to pop and win quicker\n   (intended to give more points \n   but didn't implement yet)\n7. Enjoy! :)");


  }

  @Override
  public void onDraw (int frameCount)
  {
    villageMap.load_tile();
    villageMap.load_map();

    if(!gameEnd()){
      drawInstruction();
      removeBomb(frameCount);
      drawPop(frameCount);
      moveBazzi();
      moveWoonie();
    }
    for(Bomb b: bombs){
      if(grid[b.x][b.y]!=13)
        b.draw();
    }

    drawCharacters(frameCount);
    if(!gameEnd()){
      drawHit(frameCount);
      popOpponent();
    }else drawEnd();
  }

  void drawEnd(){
    if(bazzi_end) font_big.draw(600,200,"Woonie Won!");
    else font_big.draw(600,200,"Bazzi Won!");
  }

  void moveBazzi(){
    if(!bazzi_Hit && !gameStart){
      if(GFKeyboard.isDown(GFKey.ArrowDown)){
        bazzi_pos =0;
        if(canMove(bazzi_x, bazzi_y+2)) //y+30
          bazzi_y+=2;
      }
      else if(GFKeyboard.isDown(GFKey.ArrowRight) ){
        bazzi_pos =2; //1
        if(canMove(bazzi_x+2, bazzi_y)){//x+20
          bazzi_x+=2;
        }
      }
      else if(GFKeyboard.isDown(GFKey.ArrowUp) ){
        bazzi_pos =3;  //2
        if(canMove(bazzi_x, bazzi_y-2))//y-10
          bazzi_y-=2;
      }
      else if(GFKeyboard.isDown(GFKey.ArrowLeft) ){
        bazzi_pos =1; //3
        if(canMove(bazzi_x-2, bazzi_y)){//x-20
          bazzi_x-=2;
        }
      }
    }
  }


  void moveWoonie(){
    if(!woonie_Hit && !gameStart){
      if(GFKeyboard.isDown(GFKey.D)){
        woonie_pos =0;
        if(canMove(woonie_x, woonie_y+2))
          woonie_y+=2;
      }
      else if(GFKeyboard.isDown(GFKey.F) ){
        woonie_pos =2;
        if(canMove(woonie_x+2, woonie_y))
          woonie_x+=2;
      }
      else if(GFKeyboard.isDown(GFKey.E) ){
        woonie_pos =3;
        if(canMove(woonie_x, woonie_y-2))
          woonie_y-=2;
      }
      else if(GFKeyboard.isDown(GFKey.S) ){
        woonie_pos =1;
        if(canMove(woonie_x-2, woonie_y))
          woonie_x-=2;
      }
    }
  }

  ArrayList<GFStamp> bubble_frames;
  ArrayList<GFStamp> die_frames;

  void load_die_anim(){
    die_frames = new ArrayList<GFStamp>();
    for(int i=1;i<=6;i++){
      String name = String.format("assets/die_anim/die%d.png", i);
      GFStamp s = new GFStamp(name);
      s.rescale(0.25,0.25);
      s.centerPin();
      die_frames.add(s);
    }
  }


  void load_explosion ()
  {
    bubble_frames = new ArrayList<GFStamp>();
    for (int i = 1; i <= 4; ++i)
    {
      String name = String.format("assets/bubble/apngframe%d.png", i);

      GFStamp s = new GFStamp(name);
      s.rescale(1.1,1.1);
      s.pinX = .1f;
      s.pinY = .1f;
      bubble_frames.add(s);
    }
  }
  {
    load_explosion();
    load_die_anim();
  }


}