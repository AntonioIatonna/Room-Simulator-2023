/* Change the package to the location where you are running the code (same folder as labs and assignments).
Make your changes in that folder and once you have tested and are ready to commit changes, copy and paste the files into
the repository folder, replacing the old ones. Please make sure previously working code is not broken before you commit changes
*/
// package codesAI280;
package FinalProject.ModellingThe3DWorld.codes280;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;

import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.vecmath.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.Random;

public class RoomSimulator extends JPanel implements MouseListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;

	protected static int shine = 32;                                // specify common values for object's appearance
	protected static Color3f[] mtl_clr = {new Color3f(1.000000f, 1.000000f, 1.000000f),
			new Color3f(0.772500f, 0.654900f, 0.000000f),	
			new Color3f(0.175000f, 0.175000f, 0.175000f),
			new Color3f(0.000000f, 0.000000f, 0.000000f)};
	
	private static PickTool pickTool;
	// private static String fileFormat = "codesAI280/"; // change this variable to whatever the file system requires on your computer
	private static String fileFormat = "FinalProject/ModellingThe3DWorld/codes280/"; // change this variable to whatever the file system requires on your computer
    private static final int OBJ_NUM = 30;

	static Boolean pressed;
	static Boolean pressed_left;
	static RoomObjects[] roomObjects;

	static String floorNames[];
	static int currentFloor;

	static String wallNames[];
	static int currentWall;

	static String pictureNames[];
	static int currentPicture;

	static double currAngle_L_R;
	static double currAngle_U_D;

	protected static SimpleUniverse su;

	private static int flag = 1;

	protected static SoundUtilityJOAL soundJOAL;

	private static TextureUnitState texState(String fn, TextureAttributes ta, TexCoordGeneration tcg) {
		// Loads image:
		String filename = fileFormat + "Images/" + fn;
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage();

		// Confirm if image exists:
		if (image == null)
		System.out.println("load failed for texture: " + filename);

		// Set Width, Height and Textures:
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, 
		Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		TextureUnitState tt_State = new TextureUnitState(texture, ta, tcg);
		tt_State.setCapability(TextureUnitState.ALLOW_STATE_WRITE);
		
		// Return image
		return tt_State;
	}
	
	private static Scene loadShape(String obj_name) {
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
		Scene s = null;
		try {                                              // load object's definition file to 's'
			s = f.load(fileFormat + "Objects/" + obj_name + ".obj");
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;                                          // return the object shape in 's'
	}
	
	/* a function to create the Room
	 * Created walls and floors seperately so we can easily set the texture of the floor and walls
	 */
	private static Appearance makeTexture(String file){
		Appearance appTemp = Commons.obj_Appearance(Commons.Cyan);
		// Set Transparency
		TransparencyAttributes transparency = new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, 0.0f);
		appTemp.setTransparencyAttributes(transparency);
		
		TextureUnitState[] array = new TextureUnitState[1];
		TexCoordGeneration tcg = new TexCoordGeneration();
		tcg.setEnable(false);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		array[0] = texState(file, ta, tcg);

		// Set Textures and polygon
		PolygonAttributes polyAtt = new PolygonAttributes();
		polyAtt.setCullFace(PolygonAttributes.CULL_NONE);
		appTemp.setPolygonAttributes(polyAtt);
		appTemp.setTextureUnitState(array);

		return appTemp;
	}

	/* function to create the room and place in all objects */
	private static TransformGroup createRoom() {
		TransformGroup roomTG = new TransformGroup();

		Transform3D tableTex = new Transform3D();

		roomObjects = new RoomObjects[OBJ_NUM];

		// Move the Desk
		TransformGroup deskSetup = new TransformGroup();
		Transform3D deskSetup_trsm = new Transform3D();
		Transform3D deskSetup_rot = new Transform3D();
		deskSetup_rot.rotY(Math.PI / 2);
		deskSetup_trsm.setTranslation(new Vector3f(-1.5f, -1.4f, 2.3f));
		deskSetup_trsm.mul(deskSetup_rot);
		deskSetup.setTransform(deskSetup_trsm);

		TransformGroup desktop_Items = new TransformGroup();
		
		/* initialzie all objects */
		roomObjects[0] = new TableObject(); // for table
		roomObjects[1] = new LeftSpeaker(); // left speaker
		roomObjects[2] = new RightSpeaker(); // right speaker
		roomObjects[3] = new tableMat(); // table Mat
		roomObjects[4] = new rightScreen(); // right screen
		roomObjects[5] = new leftScreen(); // left screen
		roomObjects[6] = new CPU(); // PC
		roomObjects[7] = new Chair(); // first chair tyle
		roomObjects[8] = new tableWhiteMat(); // mouse mat
		roomObjects[9] = new PictureFrame(); // picture frame
		roomObjects[10] = new Walls_Floors(); // room itself

		roomObjects[11] = new Chair2();

		roomObjects[12] = new Chair3();

		roomObjects[13] = new Radio();

		roomObjects[14] = new Bed();

		roomObjects[15] = new Shelf();

		roomObjects[16] = new Door();

		roomObjects[17] = new Matress();

		roomObjects[18] = new Pillow();

		roomObjects[19] = new BedSheets();

		roomObjects[20] = new FloatingShelf();

		roomObjects[21] = new Ball();

		roomObjects[22] = new Window();





		tableTex.setTranslation(new Vector3f(-1.2f,-0.5f,3.00f));
		desktop_Items.addChild(new Box(0.5f, 0.01f, 1.2f, Primitive.GENERATE_TEXTURE_COORDS, makeTexture("table.jpg")));
		desktop_Items.setTransform(tableTex);
		
		/* create relationships between objects so a colection can be moved by modifying a single TransformGroup */
		roomTG = roomObjects[10].position_Object(); // walls and floor
		/* group desk items */
		deskSetup.addChild(desktop_Items); // table texture
		deskSetup.addChild(roomObjects[0].position_Object()); // table
		deskSetup.addChild(roomObjects[1].position_Object()); // left speaker
		deskSetup.addChild(roomObjects[2].position_Object()); // right speaker
		deskSetup.addChild(roomObjects[3].position_Object()); // table Mat
		deskSetup.addChild(roomObjects[4].position_Object()); // right screen
		deskSetup.addChild(roomObjects[5].position_Object()); // left screen
		deskSetup.addChild(roomObjects[6].position_Object()); // PC
		deskSetup.addChild(roomObjects[8].position_Object()); // mouse mat

		roomObjects[10].add_Child(deskSetup); // add desk to room

		/* for all other objects */
		roomObjects[10].add_Child(roomObjects[7].position_Object()); // chair
		roomObjects[10].add_Child(roomObjects[9].position_Object()); // picture frame
		roomObjects[10].add_Child(roomObjects[11].position_Object()); // chair2
		roomObjects[10].add_Child(roomObjects[12].position_Object()); // chair3
		roomObjects[10].add_Child(roomObjects[13].position_Object()); // radio
		roomObjects[10].add_Child(roomObjects[15].position_Object()); // shelf
		roomObjects[10].add_Child(roomObjects[16].position_Object()); // door

		/* for collision of objects */
		roomObjects[10].add_Child(roomObjects[20].position_Object());
		roomObjects[10].add_Child(roomObjects[21].position_Object());

		roomObjects[10].add_Child(roomObjects[22].position_Object());



		TransformGroup bed_TG = new TransformGroup();
		Transform3D bed_trsm = new Transform3D();
		Transform3D bed_rot = new Transform3D();
		
		bed_trsm.setScale(1.5d);
		bed_trsm.setTranslation(new Vector3d(-1.0f, -2.2f, 2.3f));
		
		bed_rot.rotY(Math.PI / 2);
		
		bed_trsm.mul(bed_rot);
		bed_TG.setTransform(bed_trsm);

		bed_TG.addChild(roomObjects[14].position_Object()); // bed frame
		bed_TG.addChild(roomObjects[17].position_Object()); // matress
		bed_TG.addChild(roomObjects[18].position_Object()); // pillow
		bed_TG.addChild(roomObjects[19].position_Object()); // bed sheets

		roomTG.addChild(bed_TG);

		// calculate and set angles for rotation of room
		currAngle_L_R = Math.PI/2.0 * 3.0;
		currAngle_U_D = 0;

        return roomTG;
	}

	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();	   // make 'sceneTG' continuously rotating

		sceneTG.addChild(createRoom());                    // add the fan to the rotating 'sceneTG'
		sceneBG.addChild(sceneTG);                         // keep the following stationary
		sceneBG.addChild(Commons.add_Lights(Commons.White, 1));

		pickTool = new PickTool( sceneBG );                // allow picking of objects in 'sceneBG'
		pickTool.setMode(PickTool.GEOMETRY);                 // pick by bounding volume

		// Background Stuff:
		Background myBackground = new Background(); 
		// Add image: (NOTE: The file directory works on my linux machine -> if you cannot open the file -> you can change the file directory)
		myBackground.setImage(new TextureLoader(fileFormat + "Images/" + "galaxy.jpg", null).getImage()); 
		myBackground.setImageScaleMode(Background.SCALE_FIT_ALL); 

		myBackground.setApplicationBounds(sceneBG.getBounds());
		sceneBG.addChild(myBackground);

		return sceneBG;
	}

	/* Default Constructor */
	public RoomSimulator(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		canvas.addMouseListener(this);                     // NOTE: enable mouse clicking
		
		canvas.addKeyListener(this);
		pressed = false;
		pressed_left = false;
		initialSound();

		// Set Strings for Textures:
		floorNames = new String[4];
		floorNames[0] = "floor1.jpg";
		floorNames[1] = "floor2.jpg";
		floorNames[2] = "floor3.jpg";
		floorNames[3] = "floor4.jpg";
		currentFloor = 0;

		wallNames = new String[4];
		wallNames[0] = "wall2.jpg";
		wallNames[1] = "wall1.jpg";
		wallNames[2] = "wall3.jpg";
		wallNames[3] = "wall4.jpg";
		currentWall = 0;

		pictureNames = new String[7];
		pictureNames[0] = "picture1.jpg";
		pictureNames[1] = "picture2.jpg";
		pictureNames[2] = "picture3.jpg";
		pictureNames[3] = "picture4.jpg";
		pictureNames[4] = "picture5.jpg";
		pictureNames[5] = "picture6.jpg";
		pictureNames[6] = "picture7.jpg";
		currentPicture = 0;

		su = new SimpleUniverse(canvas);    // create a SimpleUniverse

		Commons.define_Viewer(su, new Point3d(15.00d, 10.0d, 15.0d));   // set the viewer's location
		
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse
		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(1920, 1080);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	/* function to initialize sound at program start */
	public static void initialSound() {
        soundJOAL = new SoundUtilityJOAL();
        if (!soundJOAL.load("music", 0f, 0f, 10f, true))
            System.out.println("Could not load " + "music");        
    }

	/* main method */
	public static void main(String[] args) {
		System.setProperty("jogl.disable.openglcore", System.getProperty("jogl.disable.openglcore", "false"));
		frame = new JFrame("Room Simulator");                   
		frame.getContentPane().add(new RoomSimulator(create_Scene()));  // start the program
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/* function to change the appearance an textures of an object */
	public static void changeAppearance(Appearance app, String fileName){
		ImageComponent2D image;       
		Texture2D texture;       
		TextureLoader textureLoader;
		
		// Load the image
		textureLoader = new TextureLoader(fileFormat + "Images/" + fileName, null);
		image = textureLoader.getImage();
	
		// Create the Texture
		texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);  // Level 0 indicates no mip-mapping

		// Create the Appearance
		app.setTexture(texture);
	}
	
	/* function to control all mouse click behaviour */
	@Override
	public void mouseClicked(MouseEvent event) {
		int x = event.getX(); int y = event.getY();        // mouse coordinates
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas.getPixelLocationInImagePlate(x, y, point3d); // obtain AWT pixel in ImagePlate coordinates
		canvas.getCenterEyeInImagePlate(center);           // obtain eye's position in IP coordinates
		
		Transform3D transform3D = new Transform3D();       // matrix to relate ImagePlate coordinates~
		canvas.getImagePlateToVworld(transform3D);         // to Virtual World coordinates
		transform3D.transform(point3d);                    // transform 'point3d' with 'transform3D'
		transform3D.transform(center);                     // transform 'center' with 'transform3D'

		Vector3d mouseVec = new Vector3d();
		mouseVec.sub(point3d, center);
		mouseVec.normalize();
		pickTool.setShapeRay(point3d, mouseVec);           // send a PickRay for intersection

		if (pickTool.pickClosest() != null) {
			PickResult pickResult = pickTool.pickClosest();// obtain the closest hit
			Node clicked = (Node) pickResult.getNode(PickResult.PRIMITIVE);

			Shape3D objectClicker = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);

			/* create instance of known clickable objcts to compare clicked object to */
			Walls_Floors temp = (Walls_Floors) roomObjects[10];
			PictureFrame temp2 = (PictureFrame) roomObjects[9];

			Radio temp3 = (Radio) roomObjects[13];
			Chair chair1 = (Chair) roomObjects[7];
			Chair2 chair2 = (Chair2) roomObjects[11];
			Chair3 chair3 = (Chair3) roomObjects[12];

			BedSheets bed_sheets = (BedSheets) roomObjects[19];
			
			/* behaviour based on what is clicked for non shape3D objects*/
			if(clicked != null){
				if(clicked.equals(temp.getFloor())){ // if floor
					Box trsm = (Box) temp.getFloor();
					currentFloor++;
					changeAppearance(trsm.getAppearance(), floorNames[currentFloor % 4]);
				}
				else if(clicked.equals(temp.getWall1()) || clicked.equals(temp.getWall2())){ // if wall
					Box trsm1 = (Box) temp.getWall1();
					Box trsm2 = (Box) temp.getWall2();
					currentWall++;
					changeAppearance(trsm1.getAppearance(), wallNames[currentWall % 4]);
					changeAppearance(trsm2.getAppearance(), wallNames[currentWall % 4]);
				}
				else if(clicked.equals(temp2.getPicture())){ // if picture
					Box trsm = (Box) temp2.getPicture();
					currentPicture++;
					changeAppearance(trsm.getAppearance(), pictureNames[currentPicture % 7]);
				}
			}
			
			/* behaviour based on what is clicked for shape3D objects*/
			if(objectClicker != null){
				if(objectClicker.equals(chair1.obj_shape)){ // if chair1
					TransformGroup chair1_TG = chair1.getTG();
					TransformGroup chair2_TG = chair2.getTG();

					Transform3D chair1_trsm = chair1.trfm;
					chair1_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.0f));

					chair1_TG.setTransform(chair1_trsm);

					Transform3D chair2_trsm = chair2.trfm;
					chair2_trsm.setTranslation(new Vector3f(1.2f, -1.7f,2.4f));

					chair2_TG.setTransform(chair2_trsm);
					moveObjectLR(roomObjects[10].getTG(), 0.05);
					moveObjectLR(roomObjects[10].getTG(), -0.05);
				}
				else if(objectClicker.equals(chair2.obj_shape)){ // if chair2
					TransformGroup chair2_TG = chair2.getTG();
					TransformGroup chair3_TG = chair3.getTG();

					Transform3D chair2_trsm = chair2.trfm;
					chair2_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.4f));

					chair2_TG.setTransform(chair2_trsm);

					Transform3D chair3_trsm = chair3.trfm;
					chair3_trsm.setTranslation(new Vector3f(1.2f, -2.0f,2.8f));

					chair3_TG.setTransform(chair3_trsm);
					moveObjectLR(roomObjects[10].getTG(), 0.05);
					moveObjectLR(roomObjects[10].getTG(), -0.05);
				}
				else if(objectClicker.equals(chair3.obj_shape)){ // if chair3
					TransformGroup chair3_TG = chair3.getTG();
					TransformGroup chair1_TG = chair1.getTG();

					Transform3D chair3_trsm = chair3.trfm;
					chair3_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.8f));

					chair3_TG.setTransform(chair3_trsm);

					Transform3D chair1_trsm = chair1.trfm;
					chair1_trsm.setTranslation(new Vector3f(1.2f, -2.0f,2.0f));

					chair1_TG.setTransform(chair1_trsm);
					moveObjectLR(roomObjects[10].getTG(), 0.05);
					moveObjectLR(roomObjects[10].getTG(), -0.05);
				}
				else if(objectClicker.equals(temp3.obj_shape)){ // if radio
					if(flag == 1){
						flag = 2;
						soundJOAL.play("music");
					}
					else if(flag == 2){
						flag = 1;
						soundJOAL.stop("music");
					}
				}
				else if(objectClicker.equals(bed_sheets.obj_shape)){ // if bed
					Random rd = new Random(); 
					Appearance app = new Appearance();
					Material mtl = new Material();                     // define material's attributes
					mtl.setShininess(shine);
					mtl.setAmbientColor(mtl_clr[0]);                   // use them to define different materials
					mtl.setDiffuseColor(new Color3f(rd.nextFloat(), rd.nextFloat(), rd.nextFloat()));
					mtl.setSpecularColor(mtl_clr[2]);
					mtl.setEmissiveColor(mtl_clr[3]);                  // use it to enlighten a button
					mtl.setLightingEnable(true);

					app.setMaterial(mtl);                              // set appearance's material

					bed_sheets.obj_shape.setAppearance(app);         
				}
			}
		}
	}

	public static void moveObjectLR(TransformGroup trsm, double angle){
		Transform3D temp = new Transform3D();

		Transform3D ty = new Transform3D();

		currAngle_L_R += angle;

		if(currAngle_L_R >= Math.PI*2.0){
			currAngle_L_R -= Math.PI*2.0;
		}

		ty.rotY(currAngle_L_R);
		temp.mul(ty);

		trsm.setTransform(temp);
	}

	/* function to control all key pressed behaviour */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			moveObjectLR(roomObjects[10].getTG(), 0.05);
		}

		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			moveObjectLR(roomObjects[10].getTG(), -0.05);
		}

		if(e.getKeyCode() == KeyEvent.VK_UP){
			Commons.define_Viewer2(su, new Point3d(0.00d, 20.0d, 0.0d));
		}

		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			Commons.define_Viewer(su, new Point3d(15.00d, 10.0d, 15.0d));
		}
	}

	/* unimplemente abstract methods */
	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}