/* Change the package to the location where you are running the code (same folder as labs and assignments).
Make your changes in that folder and once you have tested and are ready to commit changes, copy and paste the files into
the repository folder, replacing the old ones. Please make sure previously working code is not broken before you commit changes
*/
package codesAI280;

import java.awt.BorderLayout;
import java.awt.Color;
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

public class RoomSimulator extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;
	private static PickTool pickTool;
	private static String fileFormat = "codesAI280/"; // change this variable to whatever the file system requires on your computer
    
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
	

	/* a function to create the desk Room
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

		Float x = 4.0f;
		Float y = 3.0f;
		Float z = 3.0f;
		Float depth = 0.2f;

		TransformGroup roomTG = new TransformGroup();

		Transform3D trsm_wall1 = new Transform3D();
		Transform3D trsm_wall2 = new Transform3D();
		Transform3D trsm_floor = new Transform3D();

		trsm_wall1.setTranslation(new Vector3f(0.0f, -(depth), -(depth + z)));
		trsm_wall2.setTranslation(new Vector3f(-(x + depth), -(depth), 0.0f));
		trsm_floor.setTranslation(new Vector3f(0.0f, -(y), 0.0f));

		// Add Textures:

		TransformGroup floor = new TransformGroup();
		floor.addChild(new Box(x, depth, z,Primitive.GENERATE_TEXTURE_COORDS, makeTexture("floor1.jpg")));
		floor.setTransform(trsm_floor);

		TransformGroup wall1 = new TransformGroup();
		wall1.addChild(new Box(x, y, depth,Primitive.GENERATE_TEXTURE_COORDS, makeTexture("wall1.jpg")));
		wall1.setTransform(trsm_wall1);

		TransformGroup wall2 = new TransformGroup();
		wall2.addChild(new Box(depth, y, z, Primitive.GENERATE_TEXTURE_COORDS, makeTexture("wall1.jpg")));
		wall2.setTransform(trsm_wall2);

        roomTG.addChild(floor);
        roomTG.addChild(wall1);
        roomTG.addChild(wall2);

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
		Canvas3D canvas = new Canvas3D(config);
		canvas.addMouseListener(this);                     // NOTE: enable mouse clicking
		
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
            Shape3D clicked = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
			System.out.println(clicked);
            // Appearance app = new Appearance();             // originally a PRIMITIVE as a box
            // if(clicked.equals(ring1.getShape3D())){
			// 	RotationInterpolator R = (RotationInterpolator) sceneBG.getChild(3);
			// 	if(! R.getAlpha().isPaused()){
			// 		R.getAlpha().pause();
			// 	}
			// 	else{
			// 		R.getAlpha().resume();
			// 	}
			// }
        } 
	}

	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

