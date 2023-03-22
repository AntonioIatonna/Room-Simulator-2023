/* Change the package to the location where you are running the code (same folder as labs and assignments).
Make your changes in that folder and once you have tested and are ready to commit changes, copy and paste the files into
the repository folder, replacing the old ones. Please make sure previously working code is not broken before you commit changes
*/
package codesAI280;

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
		// Float x = 4.0f;
		// Float y = 3.0f;
		// Float z = 3.0f;
		// Float depth = 0.2f;

		TransformGroup roomTG = new TransformGroup();

		// floor = new TransformGroup();
		// wall1 = new TransformGroup();
		// wall2 = new TransformGroup();

		// Transform3D trsm_wall1 = new Transform3D();
		// Transform3D trsm_wall2 = new Transform3D();
		// Transform3D trsm_floor = new Transform3D();
		Transform3D tableTex = new Transform3D();

		roomObjects = new RoomObjects[OBJ_NUM];
		TransformGroup desktop_Items = new TransformGroup();
		
		roomObjects[0] = new TableObject(); //For table
		roomObjects[1] = new LeftSpeaker(); //Left speaker
		roomObjects[2] = new RightSpeaker(); //right speaker
		roomObjects[3] = new tableMat(); //Table Mat
		roomObjects[4] = new rightScreen();
		roomObjects[5] = new leftScreen();
		roomObjects[6] = new CPU();
		roomObjects[7] = new Chair();
		roomObjects[8] = new tableWhiteMat();

		roomObjects[9] = new PictureFrame();

		roomObjects[10] = new Walls_Floors();


		

		
		tableTex.setTranslation(new Vector3f(-1.2f,-0.5f,3.05f));
		desktop_Items.addChild(new Box(0.5f, 0.01f, 1.2f, Primitive.GENERATE_TEXTURE_COORDS, makeTexture("table.jpg")));
		desktop_Items.setTransform(tableTex);
		
		roomTG = roomObjects[10].position_Object(); // walls and floor
		roomObjects[10].add_Child(desktop_Items);
		roomObjects[10].add_Child(roomObjects[0].position_Object());
		roomObjects[10].add_Child(roomObjects[1].position_Object());
		roomObjects[10].add_Child(roomObjects[2].position_Object());
		roomObjects[10].add_Child(roomObjects[3].position_Object());
		roomObjects[10].add_Child(roomObjects[4].position_Object());
		roomObjects[10].add_Child(roomObjects[5].position_Object());
		roomObjects[10].add_Child(roomObjects[6].position_Object());
		roomObjects[10].add_Child(roomObjects[7].position_Object());
		roomObjects[10].add_Child(roomObjects[8].position_Object());

		roomObjects[10].add_Child(roomObjects[9].position_Object()); // picture frame

		Alpha a = roomObjects[10].get_Alpha();
		a.pause();

		// Alpha b = roomObjects[10].get_Alpha2();
		// b.pause();
		// Alpha b = roomObjects[10].get_Alpha2();
		// b.pause();
		
        return roomTG;
	}

	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();	   // make 'sceneTG' continuously rotating
		// sceneTG.addChild(Commons.rotate_Behavior(7500, sceneTG));
		
		sceneTG.addChild(createRoom());                    // add the fan to the rotating 'sceneTG'

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
		wallNames[0] = "wall1.jpg";
		wallNames[1] = "wall2.jpg";
		wallNames[2] = "wall3.jpg";
		wallNames[3] = "wall4.jpg";
		currentWall = 0;


		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
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
		image         = textureLoader.getImage();
	
		// Create the Texture
		texture       = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
										image.getWidth(), image.getHeight());
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
			Walls_Floors temp = (Walls_Floors) roomObjects[10];

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
		} 
	}

	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			if(!pressed){
				pressed = true;
				Alpha a = roomObjects[10].get_Alpha();
				a.setMode(Alpha.DECREASING_ENABLE);
				a.resume();
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			if(!pressed_left){
				pressed_left = true;
				Alpha a = roomObjects[10].get_Alpha();
				a.setMode(Alpha.INCREASING_ENABLE);
				a.resume();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			Alpha a = roomObjects[10].get_Alpha();
			a.pause();
			pressed = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			Alpha a = roomObjects[10].get_Alpha();
			a.pause();
			pressed_left = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}