import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import controlP5.*; 
import java.util.*; 
import java.util.Map.Entry; 
import http.requests.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class derankers_gui_v1 extends PApplet {








ControlP5 cp5;


String string1;
String string2[];
String string3;
String[] string3array;
String time;
String[] list;
byte ranks[] = new byte[6];
byte rank;

String[][] results = new String[128][3];

int numOfResults;

long timer1;

PFont f1, f2;

public void setup() 
{

  

  f1 = createFont("Helvetica", 20);
  f2 = createFont("Helvetica", 12);
  cp5 = new ControlP5( this );
  refresh();

  cp5.addButton("REFRESH")
    .setValue(0)
    .setPosition(0, 350)
    .setSize(700, 50)
    ;

  cp5.addButtonBar("bar")
    .setPosition(0, 0)
    .setSize(700, 50)
    .addItems(split("[GITHUB] [STEAM-PROFILE] [TRADE-LINK]", " "))
    ;


  //smooth();

  // GetRequest get = new GetRequest("https://steamcommunity.com/groups/csgo_derankers");
  // get.send();
  //string1 = get.getContent();
}

public void bar(int n) {
  if (n == 0) {
  }
  if (n == 1) {
    link("http://steamcommunity.com/id/wowrep/");
  }
  if (n == 2) {
    link("https://steamcommunity.com/tradeoffer/new/?partner=209508182&token=6Lp2hBqb");
  }
}

public void draw() {

  background( 220 );

  if ((millis() - timer1) > 60000) {//Refresh every minute
    refresh();
    timer1 = millis();
  }
}

public void REFRESH(int theValue) {//If button pressed
  timer1 = millis();
  refresh();
}

public void refresh() {
  
    numOfResults = 0;
  MenuList m = new MenuList( cp5, "menu", 700, 300 );
  m.setPosition(0, 50);
  string2 = loadStrings("https://steamcommunity.com/groups/csgo_derankers/comments"); // (/comments allows for more posts)
  
  //string2 = loadStrings("https://steamcommunity.com/groups/csgo_derankers");

  for (int i=0; i<string2.length; i++) {
    list = match(string2[i], "steam://joinlobby");

    if (list != null) {

      rank = 0;
      ranks = new byte[6];
      numOfResults++;

      string2[i] = string2[i].toLowerCase();
      //println(string2[i]);

      if ((string2[i].contains("all") || string2[i].contains("any")) == true) {
        println("Rank = ALL"); 
        rank += 1;
      }

      if (string2[i].contains("silver") == true) {
        println("Rank = SILVERS"); 
        rank += 2;
      }

      if ((string2[i].contains("nova") || string2[i].contains("gold")) == true) {
        println("Rank = GOLD NOVA"); 
        rank += 4;
      }

      if ((string2[i].contains("mg") || string2[i].contains("guardian")) == true) {
        println("Rank = MASTER GUARDIANS"); 
        rank += 8;
      }

      if ((string2[i].contains("le") || string2[i].contains("lem") || string2[i].contains("legendary") || string2[i].contains("eagle")) == true) {
        println("Rank = LE/LEM"); 
        rank += 16;
      }

      if ((string2[i].contains("supreme") || string2[i].contains("smfc") || string2[i].contains("global") || string2[i].contains("ge")) == true) {
        println("Rank = SUPREME/GLOBAL"); 
        rank += 32;
      }

      results[numOfResults - 1][2] = binary(rank, 6);

      string3 = string2[i];
      string3 = "steam://joinlobby" + split(string2[i], "steam://joinlobby")[1];
      string3 = split(string3, "<br>")[0];
      string3 = split(string3, ' ')[0];
      string3array = split(string3, '/');
      string3 = "";
      for (int j=0; j<5; j++) {
        string3 += string3array[j] + "/";
      }
      trim(string3);
      println(string3);

      results[numOfResults - 1][0] = string3; //Assigning link

      if (string3.length() != 0) {
        time = split(string2[i-4], "&nbsp;")[0];
        time = split(time, '\t')[5];
        trim(time);
        println(time);
        results[numOfResults - 1][1] = time; //Assigning time
      }
    }
  }

  for (int k=0; k<numOfResults; k++) {
    print(results[k][0]);
    print("    ");
    print(results[k][1]);
    print("    ");
    println(results[k][2]);


    //m.addItem(makeItem(results[k][0], results[k][1], results[k][2], createImage(50, 50, RGB)));
    m.addItem(makeItem(results[k][0], results[k][1], results[k][2]));
  }

  println(numOfResults);
}




/* a convenience function to build a map that contains our key-value pairs which we will 
 * then use to render each item of the menuList.
 */
public Map<String, Object> makeItem(String theHeadline, String theSubline, String theCopy) {
  Map m = new HashMap<String, Object>();
  m.put("headline", theHeadline);
  m.put("subline", theSubline);
  m.put("copy", theCopy);
  //m.put("image", theImage);
  return m;
}

public void menu(int i) {
  println("got some menu event from item with index "+i);
  GClip.copy(results[i][0]);
  link(results[i][0]);//LAUNCHES THAT MATCH
}

public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isFrom("menu")) {
    Map m = ((MenuList)theEvent.getController()).getItem(PApplet.parseInt(theEvent.getValue()));
    println("got a menu event from item : "+m);
  }
}

class MenuList extends Controller<MenuList> {

  float pos, npos;
  int itemHeight = 100;
  int scrollerLength = 100;
  List< Map<String, Object>> items = new ArrayList< Map<String, Object>>();
  PGraphics menu;
  boolean updateMenu;

  MenuList(ControlP5 c, String theName, int theWidth, int theHeight) {
    super( c, theName, 0, 0, theWidth, theHeight );
    c.register( this );
    menu = createGraphics(getWidth(), getHeight() );

    setView(new ControllerView<MenuList>() {

      public void display(PGraphics pg, MenuList t ) {
        if (updateMenu) {
          updateMenu();
        }
        if (inside() ) {
          menu.beginDraw();
          int len = -(itemHeight * items.size()) + getHeight();
          int ty = PApplet.parseInt(map(pos, len, 0, getHeight() - scrollerLength - 2, 2 ) );
          menu.fill(255);
          menu.rect(getWidth()-4, ty, 4, scrollerLength );
          menu.endDraw();
        }
        pg.image(menu, 0, 0);
      }
    }
    );
    updateMenu();
  }

  /* only update the image buffer when necessary - to save some resources */
  public void updateMenu() {
    int len = -(itemHeight * items.size()) + getHeight();
    npos = constrain(npos, len, 0);
    pos += (npos - pos) * 0.1f;
    menu.beginDraw();
    menu.noStroke();
    menu.background(255, 64 );
    menu.textFont(cp5.getFont().getFont());
    menu.pushMatrix();
    menu.translate( 0, pos );
    menu.pushMatrix();

    int i0 = PApplet.max( 0, PApplet.parseInt(map(-pos, 0, itemHeight * items.size(), 0, items.size())));
    int range = ceil((PApplet.parseFloat(getHeight())/PApplet.parseFloat(itemHeight))+1);
    int i1 = PApplet.min( items.size(), i0 + range );

    menu.translate(0, i0*itemHeight);

    for (int i=i0; i<i1; i++) {
      Map m = items.get(i);
      menu.fill(255, 100);
      menu.rect(0, 0, getWidth(), itemHeight-1 );
      menu.fill(50);
      menu.textFont(f1);
      menu.text(m.get("headline").toString(), 10, 20 );
      menu.textFont(f2);
      menu.textLeading(12);
      menu.text(m.get("subline").toString(), 10, 35 );
      menu.text(m.get("copy").toString(), 10, 50, 120, 50 );
      //menu.image(((PImage)m.get("image")), 140, 30, 88, 39 );

      if (m.get("copy").toString().charAt(5) == '1') {
        menu.image(((PImage)loadImage("anyrank.png")), 140, 30, 88, 39 );
      }

      if (m.get("copy").toString().charAt(4) == '1') {
        menu.image(((PImage)loadImage("silver1.png")), 230, 30, 88, 39 );
      } else {
        menu.image(((PImage)loadImage("silver1_dark.png")), 230, 30, 88, 39 );
      }

      if (m.get("copy").toString().charAt(3) == '1') {
        menu.image(((PImage)loadImage("nova1.png")), 320, 30, 88, 39 );
      } else {
        menu.image(((PImage)loadImage("nova1_dark.png")), 320, 30, 88, 39 );
      }

      if (m.get("copy").toString().charAt(2) == '1') {
        menu.image(((PImage)loadImage("mg1.png")), 410, 30, 88, 39 );
      } else {
        menu.image(((PImage)loadImage("mg1_dark.png")), 410, 30, 88, 39 );
      }

      if (m.get("copy").toString().charAt(1) == '1') {
        menu.image(((PImage)loadImage("le.png")), 500, 30, 88, 39 );
      } else {
        menu.image(((PImage)loadImage("le_dark.png")), 500, 30, 88, 39 );
      }

      if (m.get("copy").toString().charAt(0) == '1') {
        menu.image(((PImage)loadImage("ge.png")), 590, 30, 88, 39 );
      } else {
        menu.image(((PImage)loadImage("ge_dark.png")), 590, 30, 88, 39 );
      }


      menu.translate( 0, itemHeight );
    }
    menu.popMatrix();
    menu.popMatrix();
    menu.endDraw();
    updateMenu = abs(npos-pos)>0.01f ? true:false;
  }

  /* when detecting a click, check if the click happend to the far right, if yes, scroll to that position, 
   * otherwise do whatever this item of the list is supposed to do.
   */
  public void onClick() {
    if (getPointer().x()>getWidth()-10) {
      npos= -map(getPointer().y(), 0, getHeight(), 0, items.size()*itemHeight);
      updateMenu = true;
    } else {
      int len = itemHeight * items.size();
      int index = PApplet.parseInt( map( getPointer().y() - pos, 0, len, 0, items.size() ) ) ;
      setValue(index);
    }
  }

  public void onMove() {
  }

  public void onDrag() {
    npos += getPointer().dy() * 2;
    updateMenu = true;
  } 

  public void onScroll(int n) {
    npos += ( n * 4 );
    updateMenu = true;
  }

  public void addItem(Map<String, Object> m) {
    items.add(m);
    updateMenu = true;
  }

  public Map<String, Object> getItem(int theIndex) {
    return items.get(theIndex);
  }
}

  public void settings() {  size(700, 400, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "derankers_gui_v1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
