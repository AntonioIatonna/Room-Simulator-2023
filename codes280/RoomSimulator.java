package FinalProject.ModellingThe3DWorld.codes280;

// package codes280;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class RoomSimulator extends JPanel {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
    
	private static TextureUnitState texState(String fn, TextureAttributes ta, TexCoordGeneration tcg) {
		// Loads image:
		String filename = "FinalProject/ModellingThe3DWorld/codes280/Images/" + fn;
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
			s = f.load("FinalProject/ModellingThe3DWorld/codes280/Objects/" + obj_name + ".obj");
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
	private static TransformGroup createRoom() {

		TransformGroup roomTG = new TransformGroup();

		Transform3D trsm_wall1 = new Transform3D();
		Transform3D trsm_wall2 = new Transform3D();
		Transform3D trsm_floor = new Transform3D();

		trsm_wall1.setTranslation(new Vector3f(0.0f, -0.2f, -2.2f));
		trsm_wall2.setTranslation(new Vector3f(-2.2f, -0.2f, 0.0f));
		trsm_floor.setTranslation(new Vector3f(0.0f, -2.0f, 0.0f));

		

		// Add Textures:
		Appearance appFloor = Commons.obj_Appearance(Commons.Cyan);
		// Set Transparency
		TransparencyAttributes transparency = new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, 0.0f);
		appFloor.setTransparencyAttributes(transparency);
		

		
		TextureUnitState[] array = new TextureUnitState[1];
		TexCoordGeneration tcg = new TexCoordGeneration();
		tcg.setEnable(false);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		array[0] = texState("floor1.jpg", ta, tcg);
		// Set Textures and polygon
		PolygonAttributes polyAtt = new PolygonAttributes();
		polyAtt.setCullFace(PolygonAttributes.CULL_NONE);
		appFloor.setPolygonAttributes(polyAtt);
		appFloor.setTextureUnitState(array);

		TransformGroup floor = new TransformGroup();
		floor.addChild(new Box(2.0f, 0.2f, 2.0f, appFloor));
		floor.setTransform(trsm_floor);
		

		TransformGroup wall1 = new TransformGroup();
		wall1.addChild(new Box(2.0f, 2.0f, 0.2f, Commons.obj_Appearance(Commons.Green)));
		wall1.setTransform(trsm_wall1);

		TransformGroup wall2 = new TransformGroup();
		wall2.addChild(new Box(0.2f, 2.0f, 2.0f, Commons.obj_Appearance(Commons.Green)));
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

		return sceneBG;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public RoomSimulator(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		Commons.define_Viewer(su, new Point3d(0.25d, 0.25d, 10.0d));   // set the viewer's location
		
		// sceneBG.addChild(Commons.key_Navigation(su));               // allow key navigation
		// sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		System.setProperty("jogl.disable.openglcore", System.getProperty("jogl.disable.openglcore", "false"));
		frame = new JFrame("Room Simulator");                   
		frame.getContentPane().add(new RoomSimulator(create_Scene()));  // start the program
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

