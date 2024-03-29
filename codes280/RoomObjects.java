package codes280;

import java.io.FileNotFoundException;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PositionPathInterpolator;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureAttributes;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

// This is where our objects will go:
// use 'post' to specify location

public abstract class RoomObjects {
	protected Alpha rotationAlpha;                           // NOTE: keep for future use
	protected Alpha rotationAlpha2;                           // NOTE: keep for future use

	protected RotationInterpolator rotateInterpol;
	protected RotationInterpolator rotateInterpol2;

	protected BranchGroup objBG;                           // load external object to 'objBG'
	protected TransformGroup objTG;                        // use 'objTG' to position an object

	public TransformGroup getTG(){ return objTG; }

	protected TransformGroup objRG;                        // use 'objRG' to rotate an object
	protected double scale;                                // use 'scale' to define scaling
	protected Vector3f post;                              // use 'post' to specify location
	protected Shape3D obj_shape;
	private static String fileFormat = "codes280/"; // change this variable to whatever the file system requires on your computer

	public abstract TransformGroup position_Object();      // need to be defined in derived classes
	public abstract void add_Child(TransformGroup nextTG);
	
	public Alpha get_Alpha() { return rotationAlpha; };    // NOTE: keep for future use 
	public Alpha get_Alpha2() { return rotationAlpha2; };    // NOTE: keep for future use 

	public RotationInterpolator get_RotInterpol() { return rotateInterpol; }; 
	public RotationInterpolator get_RotInterpol2() { return rotateInterpol2; }; 

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
	
	protected void transform_Object() {
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                            // set scale for the 4x4 matrix
		scaler.setTranslation(post);                       // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);                // set the translation BG with the 4x4 matrix
		objBG = new BranchGroup();                      // use the name to identify the object 
	}

	protected Appearance app = new Appearance();
	protected int shine = 32;                                // specify common values for object's appearance
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

	protected static Appearance makeTexture(String name){
       Appearance          appearance;       
       ImageComponent2D    image;       
       Texture2D           texture;       
       TextureLoader       textureLoader;
     
	   int flags = Appearance.ALLOW_TEXTURE_READ
	   | Appearance.ALLOW_TEXTURE_WRITE
	   | Texture.ALLOW_ENABLE_READ
	   | Texture.ALLOW_ENABLE_WRITE
	   | Texture2D.ALLOW_ENABLE_READ
	   | Texture2D.ALLOW_ENABLE_WRITE
	   ;
	   
       // Load the image
       textureLoader = new TextureLoader(fileFormat + "Images/" + name, null);
       image = textureLoader.getImage();

       // Create the Texture
	
       texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
       texture.setImage(0, image);  // Level 0 indicates no mip-mapping

       // Create the Appearance
       appearance    = new Appearance();
	   appearance.setCapability(flags);
       appearance.setTexture(texture);

		TextureAttributes test = new TextureAttributes();
		test.setTextureMode(TextureAttributes.REPLACE | TextureAttributes.NICEST);

	   appearance.setTextureAttributes(test);

       return appearance;       
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
			// Code to add Lights: 
			objTG.addChild(add_Lights(Commons.Cyan, 1));   
			TransformGroup strip = new TransformGroup();
			Transform3D strip_trsm = new Transform3D();
			strip_trsm.setTranslation(new Vector3f(-0.6f, 0.4f, 0.0f));
			strip.setTransform(strip_trsm);
			strip.addChild(new Box(0.01f,0.01f,0.8f, Commons.obj_Appearance(Commons.Cyan)));
			add_Child(strip);
	
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	private BranchGroup add_Lights(Color3f clr, int p_num) {
		BranchGroup lightBG = new BranchGroup();

		AmbientLight ambientLight = new AmbientLight(clr);
		ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0f, 0.0f, 0.0f), 0.000001f));

		lightBG.addChild(ambientLight);
		return lightBG;
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class Window extends RoomObjects{
	public Transform3D trfm;
	public Window() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.3f,0f,0f);                   // use to move object for positioning
		transform_Object("window");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color
		app = makeTexture("windowpaint.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(1.0f, 0.1f,4.0f));

		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotX(Math.PI / 2);
		trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTG.addChild(objBG);
		
		TransformGroup innerWindow = new TransformGroup();
		Transform3D innerWindow_trsm = new Transform3D();
		innerWindow_trsm.setTranslation(new Vector3f(0.1f,0.0f,0.0f));
		innerWindow.setTransform(innerWindow_trsm);
		innerWindow.addChild(new Box(0.84f, 0.02f, 0.6f,Primitive.GENERATE_TEXTURE_COORDS, makeTexture("galaxy.jpg")));
		objTG.addChild(innerWindow);                          // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
	}
}

class LeftSpeaker extends RoomObjects{
	public LeftSpeaker() {
		scale = 1.2d;                                        // use to scale up/down original size
		post = new Vector3f(0f,0f,4.0f);                   // use to move object for positioning  
		transform_Object("leftSpeaker");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(-0.15f,-0.15f,4.0f));
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
		post = new Vector3f(0f,-0.15f,2.15f);                   // use to move object for positioning  
		transform_Object("rightSpeaker");                      // set transformation to 'objTG' and load object file
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
		post = new Vector3f(-0.25f,-0.45f,3.1f);                   // use to move object for positioning  
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
		translator.setTranslation(new Vector3f(-0.2f, -0.2f,2.7f));
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
		translator.setTranslation(new Vector3f(-0.2f, -0.2f,3.4f));
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
	public Transform3D trfm;
	public Chair() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("Chair");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation

		translator.setTranslation(new Vector3f(1.2f, -2.0f,2.0f));

		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(Math.PI / 2);
		trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class Chair2 extends RoomObjects{
	public Transform3D trfm;
	public Chair2() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("Chair2");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color 		              
		app = makeTexture("chair_fabric.jpg");                               
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(1.2f, 20.0f,2.4f));

		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(0);
		trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm); 
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); 
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class Chair3 extends RoomObjects{
	public Transform3D trfm;
	public Chair3() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("Chair3");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] =  Commons.White; // set  color 		                                             
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(1.2f, 20.0f,2.8f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotX(Math.PI / 2 * 3);
		trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);  
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class Shelf extends RoomObjects{
	public Shelf(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.0f,-2f,0f);                   // use to move object for positioning
		transform_Object("shelf");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.27f, 0.13f, 0.08f); // set  color
		app = makeTexture("wood.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(-2.5f, -1.7f,-0.9f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(0);
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

class Bed extends RoomObjects {
	public Bed(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.5f,1.9f,0f);                   // use to move object for positioning
		transform_Object("bedFrame");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color
		app = makeTexture("wood.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.0f, -0.2f, 0.0f));
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

class Matress extends RoomObjects {
	public Matress(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.5f,1.9f,0f);                   // use to move object for positioning
		transform_Object("matress");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(1.0f, 1.0f, 1.0f); // set  color
		app = makeTexture("mattress.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.0f, 0.0f, 0.04f));
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

class Pillow extends RoomObjects {
	public Pillow(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.5f,1.9f,0f);                   // use to move object for positioning
		transform_Object("pillow");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = Commons.White; // set  color
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.05f, 0.2f, 0.25f));
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

class BedSheets extends RoomObjects {
	public BedSheets(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(1.5f,1.9f,0f);                   // use to move object for positioning
		transform_Object("sheets");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = Commons.Green; // set  color
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		obj_shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		obj_shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		obj_shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		obj_shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);

		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.23f, 0.0f, 0.0f));
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

class Door extends RoomObjects{
	public Door(){
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(-1.2f,0f,-1f);                   // use to move object for positioning
		transform_Object("door");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.27f, 0.13f, 0.08f); // set  color
		app = makeTexture("door_wood.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(-2.92f, -1.3f,-2.8f));
		translator.setScale(1.5f);
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(Math.PI / 2);
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
		translator.setTranslation(new Vector3f(-0.05f, -0.4f,3.0f));
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

class PictureFrame extends RoomObjects{
	public PictureFrame() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0.0f,0f,0f);                   // use to move object for positioning  
		transform_Object("frame");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = Commons.Brown; // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        
		translator.setTranslation(new Vector3f(-2.95f, 0f,2.5f));
		Transform3D rotator1 = new Transform3D();  
		Transform3D rotator2 = new Transform3D();          
		rotator1.rotZ(Math.PI / 2 * 1);
		rotator2.rotY(Math.PI / 2 * 1);
		Transform3D trfm = new Transform3D();              
		trfm.mul(translator);                             
		trfm.mul(rotator1);
		trfm.mul(rotator2);
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);     
		
		// Image Inside:
		TransformGroup pic = new TransformGroup();
		pic.addChild(new Box(0.35f,0.01f,0.55f, Primitive.GENERATE_TEXTURE_COORDS, makeTexture("picture1.jpg")));
		add_Child(pic);

		return objTG;                                     
	}

	public Node getPicture(){
		TransformGroup temp = (TransformGroup) objTG.getChild(1);
		return temp.getChild(0);
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}

	public void add_Child_Hack(BranchGroup nextBG) {
		objTG.addChild(nextBG);   
	}
}

class Walls_Floors extends RoomObjects{
	TransformGroup floor;
	TransformGroup wall1;
	TransformGroup wall2;

	public Walls_Floors() {
		scale = 1.0d;                                        // use to scale up/down original size
		post = new Vector3f(0.0f,0f,0f);                   // use to move object for positioning  
		transform_Object();                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = Commons.Brown; // set  color 		                                              
	}
	
	public TransformGroup position_Object() {

		Transform3D r_axis = new Transform3D();            // default: rotate around Y-axis
		r_axis.rotY(Math.PI/2);                              // rotate around y-axis for 180 degrees
		objRG = new TransformGroup(r_axis);

		Transform3D initalRot = new Transform3D();            // default: rotate around Y-axis
		initalRot.rotY(Math.PI/2.0 * 3.0);   
		objTG = new TransformGroup(initalRot);  

		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		objTG.addChild(objRG);                             // position "FanStand" by attaching 'objRG' to 'objTG'
		objRG.addChild(objBG);  
		  
		Float x = 4.0f;
		Float y = 3.0f;
		Float z = 3.0f;
		Float depth = 0.2f;

		floor = new TransformGroup();
		wall1 = new TransformGroup();
		wall2 = new TransformGroup();

		Transform3D trsm_wall1 = new Transform3D();
		Transform3D trsm_wall2 = new Transform3D();
		Transform3D trsm_floor = new Transform3D();

		trsm_wall1.setTranslation(new Vector3f(0.0f, -(depth), -(depth + z)));
		trsm_wall2.setTranslation(new Vector3f(-(x + depth), -(depth), 0.0f));
		trsm_floor.setTranslation(new Vector3f(0.0f, -(y), 0.0f));

		int flags = Primitive.GENERATE_TEXTURE_COORDS 
		| Primitive.ENABLE_GEOMETRY_PICKING 
		| Box.ENABLE_APPEARANCE_MODIFY;

		// Add Textures:
		floor.addChild(new Box(x, depth, z, flags, makeTexture("floor1.jpg")));

		floor.setTransform(trsm_floor);

		TransformGroup wall1Blocker = new TransformGroup();
		Transform3D wall1Blocker_trsm = new Transform3D();
		wall1Blocker_trsm.setTranslation(new Vector3f(0.0f, 0.0f, -(depth + 0.01f)));

		wall1Blocker.addChild(new Box(x, y, 0.01f, flags, Commons.obj_Appearance(Commons.Black)));
		wall1Blocker.setTransform(wall1Blocker_trsm);
		wall1.addChild(wall1Blocker);
		
		wall1.addChild(new Box(x, y, depth,Primitive.GENERATE_TEXTURE_COORDS | Primitive.ENABLE_GEOMETRY_PICKING, makeTexture("wall2.jpg")));
		wall1.setTransform(trsm_wall1);

		TransformGroup wall2Blocker = new TransformGroup();
		Transform3D wall2Blocker_trsm = new Transform3D();
		wall2Blocker_trsm.setTranslation(new Vector3f(-(depth + 0.01f), 0.0f, 0.0f));

		wall2Blocker.addChild(new Box(0.01f, y, z, flags, Commons.obj_Appearance(Commons.Black)));
		wall2Blocker.setTransform(wall2Blocker_trsm);
		wall2.addChild(wall2Blocker);
		
		wall2.addChild(new Box(depth, y, z, Primitive.GENERATE_TEXTURE_COORDS| Primitive.ENABLE_GEOMETRY_PICKING, makeTexture("wall2.jpg")));
		wall2.setTransform(trsm_wall2);

        objBG.addChild(floor);
        objBG.addChild(wall1);
        objBG.addChild(wall2);

		return objTG;                                     
	}

	public Node getFloor(){
		return floor.getChild(0);
	}
	public Node getWall1(){
		return wall1.getChild(1);
	}
	public Node getWall2(){
		return wall2.getChild(1);
	}
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class Radio extends RoomObjects{
	public Radio() {
		scale = 0.2d;                                        // use to scale up/down original size
		post = new Vector3f(2f,1f,2f);                   // use to move object for positioning  
		transform_Object("radioFull");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.15f, 0.15f, 0.15f); // set  color 		                                              
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}
	
	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setScale(0.7f);
		translator.setTranslation(new Vector3f(-5.1f,-2.0f,-1.8f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotX(Math.PI / 2 * 3);
		
		Transform3D rotator1 = new Transform3D();           // 4x4 matrix for rotation
		rotator1.rotZ(Math.PI / 2);
		
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.setScale(0.5);
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		trfm.mul(rotator1);  
		objTG = new TransformGroup(trfm);  
		objTG.addChild(objBG);                             // attach  to 'objTG'
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   
	}
}

class FloatingShelf extends RoomObjects{
	public FloatingShelf(){
		scale = 2.0d;                                        // use to scale up/down original size
		post = new Vector3f(0.0f,0.0f,0.0f);                   // use to move object for positioning
		transform_Object("floatingShelf");                      // set transformation to 'objTG' and load object file
		mtl_clr[1] = new Color3f(0.27f, 0.13f, 0.08f); // set  color
		app = makeTexture("floor2.jpg");
		obj_Appearance();                                  // set appearance after converting object node to Shape3D
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(-1.6f, -0.5f,2.0f));
		translator.setScale(2.0f);
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(Math.PI / 2);
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

class Ball extends RoomObjects{
	public Ball(){
		mtl_clr[1] = Commons.Red; // set  color
	}

	public TransformGroup position_Object() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0.0f,0.0f,0.0f));

		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(0);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);

		// Transform Thing
		Point3f[] points = new Point3f[19]; points[18] = new Point3f(-1.6f, -3.5f,-0.5f);
		
		float startZ = 3.7f;
		for (int i = 0; i < 18; i++) {
			float z = (float)(((startZ-(-0.5))/19)*i); 
			float y = (float)(-0.603* Math.pow(z,2) + 2.918*z -2.89);
			points[i] = new Point3f(-1.6f, y, z); 
		} // define position path
		
		float[] knots = new float[19]; 
		knots[18] = 1.0f;
		
		for (int i = 0; i < 18; i++) knots[i] = 1.0f * i / 17f;

		TransformGroup baseTG = new TransformGroup(); // allow 'baseTG' to change
		baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Alpha alpha = new Alpha();

		alpha.setLoopCount(-1);

		alpha.setMode(Alpha.DECREASING_ENABLE | Alpha.INCREASING_ENABLE);
		alpha.setIncreasingAlphaRampDuration(2500);
		alpha.setDecreasingAlphaRampDuration(2500);

		alpha.setDecreasingAlphaDuration(2500);
		alpha.setIncreasingAlphaDuration(2500);

		alpha.setAlphaAtOneDuration(0);
		alpha.setAlphaAtZeroDuration(0);

		Transform3D axis_t = new Transform3D();
		PositionPathInterpolator positionPathInterpol = new PositionPathInterpolator(alpha, baseTG, axis_t, knots, points);
		positionPathInterpol.setSchedulingBounds(new BoundingSphere());
		
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(Commons.Red);                     // set column's color and make changeable
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setColoringAttributes(ca);

		Sphere shape = new Sphere(0.1f, app);
		baseTG.addChild(shape);    

		CollisionDetectShapes cd = new CollisionDetectShapes(shape);
		cd.setSchedulingBounds(Commons.twentyBS);        // detect column's collision

		baseTG.addChild(cd);     

		objTG.addChild(baseTG);
		objTG.addChild(positionPathInterpol);
		objTG.addChild(objBG);                             // attach  to 'objTG'
		
		return objTG;                                      // use 'objTG' to attach  to the previous TG
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
	}
}