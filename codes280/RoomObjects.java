/* Change the package to the location where you are running the code (same folder as labs and assignments).
Make your changes in that folder and once you have tested and are ready to commit changes, copy and paste the files into
the repository folder, replacing the old ones. Please make sure previously working code is not broken before you commit changes
*/
package codesAI280;

import java.io.FileNotFoundException;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3f;

// This is where our objects will go:
                            // use 'post' to specify location

public abstract class RoomObjects {
	private Alpha rotationAlpha;                           // NOTE: keep for future use
	protected BranchGroup objBG;                           // load external object to 'objBG'
	protected TransformGroup objTG;                        // use 'objTG' to position an object
	protected TransformGroup objRG;                        // use 'objRG' to rotate an object
	protected double scale;                                // use 'scale' to define scaling
	protected Vector3f post;                              // use 'post' to specify location
	protected Shape3D obj_shape;
    private static String fileFormat = "codesAI280/"; // change this variable to whatever the file system requires on your computer
	
	public abstract TransformGroup position_Object();      // need to be defined in derived classes
	public abstract void add_Child(TransformGroup nextTG);
	
	private Scene loadShape(String obj_name) {
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
	/* function to set 'objTG' and attach object after loading the model from external file */
	protected void transform_Object(String obj_name) {
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                            // set scale for the 4x4 matrix
		scaler.setTranslation(post);                       // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);                // set the translation BG with the 4x4 matrix
		objBG = loadShape(obj_name).getSceneGroup();       // load external object to 'objBG'
		obj_shape = (Shape3D) objBG.getChild(0);           // get and cast the object to 'obj_shape'
		obj_shape.setName(obj_name);                       // use the name to identify the object 
	}
	
	protected Appearance app = new Appearance();
	private int shine = 32;                                // specify common values for object's appearance
	protected Color3f[] mtl_clr = {new Color3f(1.000000f, 1.000000f, 1.000000f),
			new Color3f(0.772500f, 0.654900f, 0.000000f),	
			new Color3f(0.175000f, 0.175000f, 0.175000f),
			new Color3f(0.000000f, 0.000000f, 0.000000f)};
	
    /* a function to define object's material and use it to set object's appearance */
	protected void obj_Appearance() {		
		Material mtl = new Material();                     // define material's attributes
		mtl.setShininess(shine);
		mtl.setAmbientColor(mtl_clr[0]);                   // use them to define different materials
		mtl.setDiffuseColor(mtl_clr[1]);
		mtl.setSpecularColor(mtl_clr[2]);
		mtl.setEmissiveColor(mtl_clr[3]);                  // use it to enlighten a button
		mtl.setLightingEnable(true);

		app.setMaterial(mtl);                              // set appearance's material
		obj_shape.setAppearance(app);                      // set object's appearance
	}	
}
class TableObject extends RoomObjects{
	public TableObject() {
		scale = 1.2d;                                        // use to scale up/down original size
		post = new Vector3f(-0.5f,-1.0f, 3.0f);                   // use to move object for positioning  
		transform_Object("desktopTable");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class LeftSpeaker extends RoomObjects{
	public LeftSpeaker() {
		scale = 1.2d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,4.2f);                   // use to move object for positioning  
		transform_Object("leftSpeaker");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0f,0f,4.3f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class RightSpeaker extends RoomObjects{
	public RightSpeaker() {
		scale = 1.2d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,2.3f);                   // use to move object for positioning  
		transform_Object("RightSpeaker");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class tableMat extends RoomObjects{
	public tableMat() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(-0.1f,-0.4f,3.1f);                   // use to move object for positioning  
		transform_Object("tableMat");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.35f, 0.35f, 0.35f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class rightScreen extends RoomObjects{
	public rightScreen() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,0f);                   // use to move object for positioning  
		transform_Object("rightScreen");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.0f, 0.0f, 0.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0f, 0f,3.0f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class leftScreen extends RoomObjects{
	public leftScreen() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,0f);                   // use to move object for positioning  
		transform_Object("leftScreen");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.0f, 0.0f, 0.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0f, 0f,3.67f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class CPU extends RoomObjects{
	public CPU() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,0f);                   // use to move object for positioning  
		transform_Object("CPU");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.0f, 0.0f, 0.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(-0.1f, -1.0f,4.0f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class Chair extends RoomObjects{
	public Chair() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("Chair");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0f, -1.0f,2.8f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
class tableWhiteMat extends RoomObjects{
	public tableWhiteMat() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("tablewhiteMat");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.5f, -0.1f,3.3f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}
