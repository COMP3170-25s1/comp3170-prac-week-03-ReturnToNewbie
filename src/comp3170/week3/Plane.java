package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static comp3170.Math.TAU;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Plane {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;

	private Shader shader;
	
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f transMatrix = new Matrix4f();
	private Matrix4f rotMatrix = new Matrix4f();
	private Matrix4f scalMatrix = new Matrix4f();
	
	final private float ROTATION = TAU/3;
	//Vector2f scale = new Vector2f(0.1f, 0.1f);
	final private float SCALE = 0.1f;
	final private Vector3f OFFSET = new Vector3f(0.25f, 0.0f, 0.0f);

	public Plane() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);

		//this is where we do translation
		
		
		//translationMatrix(offset, transMatrix);
		//scaleMatrix(scale, scalMatrix);
	    //rotationMatrix(rotation, rotMatrix);
		
		//modelMatrix.translate(OFFSET).rotateZ(rotation).scale(scale);
	    
	    //modelMatrix.mul(transMatrix).mul(rotMatrix).mul(scalMatrix); // T R S order
	    
		modelMatrix.scale(SCALE);

	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);
		
		//shader
		shader.setUniform("u_modelMatrix", modelMatrix);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}
	
	
	public void update() {
		
		modelMatrix.rotateZ(ROTATION);
		
	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */
	

	public static Matrix4f translationMatrix(Vector2f vec, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(vec.x);
		dest.m31(vec.y);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		
		//       [ cos(a) -sin(a) 0 0]
		//R(a)=  [ sin(a) cos(a)  0 0]
		//       [   0      0     0 0]
		//       [   0      0     0 1]
		
		dest.m00((float) Math.cos(angle));
		dest.m01((float) Math.sin(angle));
		dest.m10((float) Math.sin(-angle));
		dest.m11((float) Math.cos(angle));
		
		

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(Vector2f s, Matrix4f dest) {
		
		//       [   sx    0   0 0]
		//R(a)=  [   0     sy  0 0]
		//       [   0     0   0 0]
		//       [   0     0   0 1]
		
		dest.m00(s.x);
		dest.m11(s.y);

		return dest;
	}
	

}
