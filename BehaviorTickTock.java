/* Copyright material for students taking COMP-2800 to work on assignment/labs/projects. */

package FinalProject.ModellingThe3DWorld;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.j3d.examples.collision.Box;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

import FinalProject.ModellingThe3DWorld.codes280.Commons;

public class BehaviorTickTock extends JPanel {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;

	private static TransformGroup createColumn(double scale, Vector3d pos) {
		Transform3D transM = new Transform3D();
		transM.set(scale, pos);                            // Create baseTG with 'scale' and 'position'
		TransformGroup baseTG = new TransformGroup(transM);

		Shape3D shape = new Box(0.5, 5.0, 1.0);
		baseTG.addChild(shape);                            // Create a column as a box and add to 'baseTG'

		Appearance app = shape.getAppearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(0.6f, 0.3f, 0.0f);                     // set column's color and make changeable
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setColoringAttributes(ca);

		// CollisionDetectShapes cd = new CollisionDetectShapes(shape);
		// cd.setSchedulingBounds(Commons.twentyBS);        // detect column's collision

		// baseTG.addChild(cd);                               // add column with behavior of CollisionDector
		return baseTG;
	}

	private static TransformGroup createBox() {
		TransformGroup transfmTG[] = new TransformGroup[2];
		for (int i = 0; i < 2; i++) {                      // two TGs: 0-self and 1-orbit
			transfmTG[i] = new TransformGroup();
			transfmTG[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		}

		Transform3D tmp = new Transform3D();               // define scale and position
		tmp.set(0.12f, new Vector3d(0.0, -0.6, 0.0));
		TransformGroup transCube = new TransformGroup(tmp);
		transCube.addChild(new ColorCube());               // new ColorCube()
		transfmTG[0].addChild(transCube);                  // add a unit cube to 3rd TG

		Transform3D yAxis1 = new Transform3D();
		yAxis1.rotX(Math.PI / 2.0);                        // define animation along orbit
		Alpha alphaOrbit = new Alpha(-1, Alpha.INCREASING_ENABLE |
				Alpha.DECREASING_ENABLE, 0, 0, 5000, 2500, 200,	5000, 2500, 200);
		RotationInterpolator tickTock = new RotationInterpolator(alphaOrbit,
				transfmTG[1], yAxis1, -(float)Math.PI/ 2.0f, (float)Math.PI/ 2.0f);
		tickTock.setSchedulingBounds(Commons.twentyBS);
		transfmTG[1].addChild(tickTock);                   // add orbit animation to scene graph

		Transform3D yAxis2 = new Transform3D();
		Alpha alphaSelf = new Alpha(-1, Alpha.INCREASING_ENABLE,
				0, 0, 4000, 0, 0, 0, 0, 0);                // define self-rotating animation
		RotationInterpolator rotatorSelf = new RotationInterpolator(alphaSelf,
				transfmTG[0], yAxis2, 0.0f,	(float) Math.PI * 2.0f);
		rotatorSelf.setSchedulingBounds(Commons.twentyBS);
		transfmTG[0].addChild(rotatorSelf);
		transfmTG[1].addChild(transfmTG[0]);               // add self-rotation to orbit

		return transfmTG[1];
		}


	private static void createContent(TransformGroup scene_TG) {
		Vector3d[] pos = {new Vector3d(-0.52, 0.0, 0.0),
				new Vector3d(0.52, 0.0, 0.0)};
		for (int i =0; i < 2; i++)
			scene_TG.addChild(createColumn(0.12, pos[i]));
		scene_TG.addChild(createBox());
	}

	/* a function to create and return the scene BranchGroup */
	public static BranchGroup create_Scene() {
		BranchGroup scene = new BranchGroup();             // create 'scene' as content branch
		TransformGroup scene_TG = new TransformGroup();    // create 'scene_TG' TransformGroup
		scene.addChild(Commons.rotate_Behavior(7500, scene_TG));

		createContent(scene_TG);
		scene.addChild(scene_TG);                          // add scene_TG to scene BG

		scene.compile();                                   // optimize scene BG

		return scene;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public BehaviorTickTock(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		Commons.define_Viewer(su, new Point3d(4.0d, 0.5d, 1.0d));   // set the viewer's location
		
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		System.setProperty("jogl.disable.openglcore", System.getProperty("jogl.disable.openglcore", "false"));
		frame = new JFrame("BehaviorTickTock.java");       // create an instance of the class
		frame.getContentPane().add(new BehaviorTickTock(create_Scene()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}