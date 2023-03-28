/* Change the package to the location where you are running the code (same folder as labs and assignments).
Make your changes in that folder and once you have tested and are ready to commit changes, copy and paste the files into
the repository folder, replacing the old ones. Please make sure previously working code is not broken before you commit changes
*/
package codesAI280;
// package FinalProject.ModellingThe3DWorld.codes280;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jdesktop.j3d.examples.Resources;
import org.jdesktop.j3d.examples.sound.SimpleSoundsBehavior;
import org.jdesktop.j3d.examples.sound.audio.JOALMixer;
import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;

import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.vecmath.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RoomSimulator extends JPanel implements MouseListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;

	private static PickTool pickTool;
	private static String fileFormat = "codesAI280/"; // change this variable to whatever the file system requires on your computer
    private static final int OBJ_NUM = 20;

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

	// variables for sound
	private static URL[] url;
	private static BackgroundSound sound1;
	private static PointSound sound2;
	private static PointSound sound3;
	private static int flag = 0;

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
		
		roomObjects[0] = new TableObject(); // for table
		roomObjects[1] = new LeftSpeaker(); // left speaker
		roomObjects[2] = new RightSpeaker(); // right speaker
		roomObjects[3] = new tableMat(); // table Mat
		roomObjects[4] = new rightScreen(); // right screen
		roomObjects[5] = new leftScreen(); // left screen
		roomObjects[6] = new CPU(); // PC
		roomObjects[7] = new Chair(); // chair
		roomObjects[8] = new tableWhiteMat(); // mouse mat

		roomObjects[9] = new PictureFrame(); // picture frame

		roomObjects[10] = new Walls_Floors(); // room itself

		roomObjects[11] = new Chair2();

		roomObjects[12] = new Chair3();

		roomObjects[13] = new Radio();

		roomObjects[14] = new Bed();

		roomObjects[15] = new Shelf();

		roomObjects[16] = new Door();

		tableTex.setTranslation(new Vector3f(-1.2f,-0.5f,3.00f));
		desktop_Items.addChild(new Box(0.5f, 0.01f, 1.2f, Primitive.GENERATE_TEXTURE_COORDS, makeTexture("table.jpg")));
		desktop_Items.setTransform(tableTex);
		
		roomTG = roomObjects[10].position_Object(); // walls and floor
		deskSetup.addChild(desktop_Items); // table texture
		deskSetup.addChild(roomObjects[0].position_Object()); // table
		deskSetup.addChild(roomObjects[1].position_Object()); // left speaker
		deskSetup.addChild(roomObjects[2].position_Object()); // right speaker
		deskSetup.addChild(roomObjects[3].position_Object()); // table Mat
		deskSetup.addChild(roomObjects[4].position_Object()); // right screen
		deskSetup.addChild(roomObjects[5].position_Object()); // left screen
		deskSetup.addChild(roomObjects[6].position_Object()); // PC
		deskSetup.addChild(roomObjects[8].position_Object()); // mouse mat

		roomObjects[10].add_Child(deskSetup);

		roomObjects[10].add_Child(roomObjects[7].position_Object()); // chair

		roomObjects[10].add_Child(roomObjects[9].position_Object()); // picture frame
		roomObjects[10].add_Child(roomObjects[11].position_Object()); // chair2
		roomObjects[10].add_Child(roomObjects[12].position_Object()); // chair3
		roomObjects[10].add_Child(roomObjects[13].position_Object()); // radio
		roomObjects[10].add_Child(roomObjects[14].position_Object()); //bed
		roomObjects[10].add_Child(roomObjects[15].position_Object()); // shelf
		roomObjects[10].add_Child(roomObjects[16].position_Object()); // door

		// calculate and set angles for rotation
		currAngle_L_R = Math.PI/2.0 * 3.0;
		currAngle_U_D = 0;

		// roomTG.addChild(player);

        return roomTG;
	}

	private void enableAudio(SimpleUniverse simple_U) {
        JOALMixer mixer = null;  // create a joalmixer
        Viewer viewer = simple_U.getViewer();
        viewer.getView().setBackClipDistance(20.0f); // disappear beyond 20f 
        if (mixer == null && viewer.getView().getUserHeadToVworldEnable()) {  
        	mixer = new JOALMixer(viewer.getPhysicalEnvironment());
			if (!mixer.initialize()) { // add audio device
				System.out.println("Open AL failed to init");
				viewer.getPhysicalEnvironment().setAudioDevice(null);
			} 
		} 
	}

	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();	   // make 'sceneTG' continuously rotating
		// sceneTG.addChild(Commons.rotate_Behavior(7500, sceneTG));
		
		sceneTG.addChild(createRoom());                    // add the fan to the rotating 'sceneTG'

		url = new URL[3];
		// create all sounds; must be 3 in order for it to work
		url[0] = Resources.getResource(fileFormat + "Sounds/" + "music.wav"); 
        url[1] = Resources.getResource(fileFormat + "Sounds/" + "music2.wav");
        url[2] = Resources.getResource(fileFormat + "Sounds/" + "music2.wav"); 

		sound1 = new BackgroundSound();
		sound2 = new PointSound();
		sound3 = new PointSound();

		// big mess of capability setting in order to get it to work somebody clean this up

        sound1.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound1.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
        sound1.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
        sound1.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
        sound1.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
        sound1.setCapability(PointSound.ALLOW_RELEASE_WRITE);
        sound1.setCapability(PointSound.ALLOW_DURATION_READ);
        sound1.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
        sound1.setCapability(PointSound.ALLOW_LOOP_WRITE);
		sound1.setCapability(PointSound.ALLOW_POSITION_WRITE);
		sound1.setCapability(PointSound.ALLOW_MUTE_READ);
		sound1.setCapability(PointSound.ALLOW_MUTE_WRITE);
		sound1.setCapability(PointSound.ALLOW_PAUSE_READ);
		sound1.setCapability(PointSound.ALLOW_PAUSE_WRITE);

		sound2.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound2.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
        sound2.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
        sound2.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
        sound2.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
        sound2.setCapability(PointSound.ALLOW_RELEASE_WRITE);
        sound2.setCapability(PointSound.ALLOW_DURATION_READ);
        sound2.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
        sound2.setCapability(PointSound.ALLOW_POSITION_WRITE);
        sound2.setCapability(PointSound.ALLOW_LOOP_WRITE);
		sound2.setCapability(PointSound.ALLOW_MUTE_READ);
		sound2.setCapability(PointSound.ALLOW_MUTE_WRITE);
		sound2.setCapability(PointSound.ALLOW_PAUSE_READ);
		sound2.setCapability(PointSound.ALLOW_PAUSE_WRITE);

		sound3.setCapability(PointSound.ALLOW_MUTE_READ);
		sound3.setCapability(PointSound.ALLOW_MUTE_WRITE);
		sound3.setCapability(PointSound.ALLOW_PAUSE_READ);
		sound3.setCapability(PointSound.ALLOW_PAUSE_WRITE);
        sound3.setCapability(PointSound.ALLOW_LOOP_WRITE);
        sound3.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound3.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
        sound3.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
        sound3.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
        sound3.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
        sound3.setCapability(PointSound.ALLOW_RELEASE_WRITE);
        sound3.setCapability(PointSound.ALLOW_DURATION_READ);
        sound3.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
        sound3.setCapability(PointSound.ALLOW_POSITION_WRITE);

		// add sounds to Transform Group of the Scene
		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        sound1.setSchedulingBounds(soundBounds);
        sound2.setSchedulingBounds(soundBounds);
        sound3.setSchedulingBounds(soundBounds);
		sceneTG.addChild(sound1);
		sceneTG.addChild(sound2);
		sceneTG.addChild(sound3);

		// Create a sound player
		SimpleSoundsBehavior player = new SimpleSoundsBehavior(sound1, sound2, sound3, url[0], url[1], url[2], soundBounds);
		player.setSchedulingBounds(soundBounds);

		sceneTG.addChild(player);

		sceneBG.addChild(sceneTG);                         // keep the following stationary
		sceneBG.addChild(Commons.add_Lights(Commons.White, 1));

		pickTool = new PickTool( sceneBG );                // allow picking of objects in 'sceneBG'
		pickTool.setMode(PickTool.GEOMETRY);                 // pick by bounding volume

		return sceneBG;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public RoomSimulator(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		canvas.addMouseListener(this);                     // NOTE: enable mouse clicking
		
		canvas.addKeyListener(this);
		pressed = false;
		pressed_left = false;

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
		
		// sceneBG.addChild(Commons.key_Navigation(su));               // allow key navigation
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse
		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(1920, 1080);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		System.setProperty("jogl.disable.openglcore", System.getProperty("jogl.disable.openglcore", "false"));
		frame = new JFrame("Room Simulator");                   
		frame.getContentPane().add(new RoomSimulator(create_Scene()));  // start the program
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void changeAppearance(Appearance app, String fileName){
		ImageComponent2D    image;       
		Texture2D           texture;       
		TextureLoader       textureLoader;
		
		// Load the image
		textureLoader = new TextureLoader(fileFormat + "Images/" + fileName, null);
		image = textureLoader.getImage();
	
		// Create the Texture
		texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);  // Level 0 indicates no mip-mapping
		// Create the Appearance
		app.setTexture(texture);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		int x = event.getX(); int y = event.getY();        // mouse coordinates
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas.getPixelLocationInImagePlate(x, y, point3d);// obtain AWT pixel in ImagePlate coordinates
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

			Walls_Floors temp = (Walls_Floors) roomObjects[10];
			PictureFrame temp2 = (PictureFrame) roomObjects[9];
			
			Radio temp3 = (Radio) roomObjects[13];
			Chair chair1 = (Chair) roomObjects[7];
			Chair2 chair2 = (Chair2) roomObjects[11];
			Chair3 chair3 = (Chair3) roomObjects[12];
			
			// System.out.println(objectClicker);
			if(clicked != null){
				if(clicked.equals(temp.getFloor())){
					Box trsm = (Box) temp.getFloor();
					currentFloor++;
					changeAppearance(trsm.getAppearance(), floorNames[currentFloor % 4]);
				}
				if(clicked.equals(temp.getWall1()) || clicked.equals(temp.getWall2())){
					Box trsm1 = (Box) temp.getWall1();
					Box trsm2 = (Box) temp.getWall2();
					currentWall++;
					changeAppearance(trsm1.getAppearance(), wallNames[currentWall % 4]);
					changeAppearance(trsm2.getAppearance(), wallNames[currentWall % 4]);
				}
				else if(clicked.equals(temp2.getPicture())){
					Box trsm = (Box) temp2.getPicture();
					currentPicture++;
					changeAppearance(trsm.getAppearance(), pictureNames[currentPicture % 7]);
				}
			}
			
			if(objectClicker != null){
				if(objectClicker.equals(chair1.obj_shape)){
					TransformGroup chair1_TG = chair1.getTG();
					TransformGroup chair2_TG = chair2.getTG();

					Transform3D chair1_trsm = chair1.trfm;
					chair1_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.0f));

					chair1_TG.setTransform(chair1_trsm);

					Transform3D chair2_trsm = chair2.trfm;
					chair2_trsm.setTranslation(new Vector3f(1.2f, -1.7f,2.4f));

					chair2_TG.setTransform(chair2_trsm);
				}

				else if(objectClicker.equals(chair2.obj_shape)){
					TransformGroup chair2_TG = chair2.getTG();
					TransformGroup chair3_TG = chair3.getTG();

					Transform3D chair2_trsm = chair2.trfm;
					chair2_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.4f));

					chair2_TG.setTransform(chair2_trsm);

					Transform3D chair3_trsm = chair3.trfm;
					chair3_trsm.setTranslation(new Vector3f(1.2f, -2.0f,2.8f));

					chair3_TG.setTransform(chair3_trsm);
				}

				else if(objectClicker.equals(chair3.obj_shape)){
					TransformGroup chair3_TG = chair3.getTG();
					TransformGroup chair1_TG = chair1.getTG();

					Transform3D chair3_trsm = chair3.trfm;
					chair3_trsm.setTranslation(new Vector3f(1.2f, 20.0f,2.8f));

					chair3_TG.setTransform(chair3_trsm);

					Transform3D chair1_trsm = chair1.trfm;
					chair1_trsm.setTranslation(new Vector3f(1.2f, -2.0f,2.0f));

					chair1_TG.setTransform(chair1_trsm);
				}

				else if(objectClicker.equals(temp3.obj_shape)){
					if(flag == 0){
						flag = 1;
						enableAudio(su);
					}
					else if(flag == 1){
						flag = 2;
						sound1.setPause(true);
						sound2.setPause(true);
						sound3.setPause(true);
					}
					else if(flag == 2){
						flag = 1;
						sound1.setPause(false);
						sound2.setPause(false);
						sound3.setPause(false);
					}
				}
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }


	public static void moveObjectLR(TransformGroup trsm, double angle){
		Transform3D temp = new Transform3D();

		// Transform3D tx = new Transform3D();
		Transform3D ty = new Transform3D();
		// Transform3D tz = new Transform3D();

		currAngle_L_R += angle;
		// currAngle_U_D += angle;

		if(currAngle_L_R >= Math.PI*2.0){
			currAngle_L_R -= Math.PI*2.0;
		}

		// tx.rotX(currAngle_U_D);
		// temp.mul(tx);

		ty.rotY(currAngle_L_R);
		temp.mul(ty);

		// tz.rotZ(currAngle_U_D);
		// temp.mul(tz);

		trsm.setTransform(temp);
	}

	// public static void moveObjectUD(TransformGroup trsm, double angle){
	// 	Transform3D temp = new Transform3D();


	// 	Transform3D tx = new Transform3D();
	// 	Transform3D ty = new Transform3D();
	// 	Transform3D tz = new Transform3D();

	// 	// currAngle_L_R += angle;
	// 	currAngle_U_D += angle;

	// 	if(currAngle_U_D >= Math.PI*2.0){
	// 		currAngle_U_D -= Math.PI*2.0;
	// 	}

	// 	// tx.rotX(currAngle_U_D);
	// 	// temp.mul(tx);

	// 	ty.rotY(currAngle_L_R);
	// 	temp.mul(ty);

	// 	// tz.rotZ(currAngle_U_D);
	// 	// temp.mul(tz);

	// 	trsm.setTransform(temp);
	// }

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

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}