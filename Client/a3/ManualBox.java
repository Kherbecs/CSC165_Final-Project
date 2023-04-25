package a3;
import tage.*;
import tage.shapes.*;
import org.joml.*;

class ManualBox extends ManualObject {
    private float[] vertices = new float[] {
        -1.0f,  1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, // back face lower left
	    1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  -1.0f,  1.0f, -1.0f, // back face upper right
	    1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f,  1.0f, -1.0f, // right face lower back
	    1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, -1.0f, // right face upper front
	    1.0f, -1.0f,  1.0f,  -1.0f, -1.0f,  1.0f,  1.0f,  1.0f,  1.0f, // front face lower right
	    -1.0f, -1.0f,  1.0f,  -1.0f,  1.0f,  1.0f,  1.0f,  1.0f,  1.0f, // front face upper left
	    -1.0f, -1.0f,  1.0f,  -1.0f, -1.0f, -1.0f,  -1.0f,  1.0f,  1.0f, // left face lower front
	    -1.0f, -1.0f, -1.0f,  -1.0f,  1.0f, -1.0f,  -1.0f,  1.0f,  1.0f, // left face upper back
	    -1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f, // bottom face right front
	    1.0f, -1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,  -1.0f, -1.0f,  1.0f, // bottom face left back
    };
    private float[] texCoords = new float[] {
        1.0f, 1.0f,  1.0f, 0.0f,  0.0f, 0.0f, // back face
	    0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f,
	    1.0f, 0.0f,  0.0f, 0.0f,  1.0f, 1.0f, // right face
	    0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f,
	    1.0f, 0.0f,  0.0f, 0.0f,  1.0f, 1.0f, // front face
	    0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f,
	    1.0f, 0.0f,  0.0f, 0.0f,  1.0f, 1.0f, // left face
	    0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f,
	    0.0f, 1.0f,  1.0f, 1.0f,  1.0f, 0.0f, // bottom face
	    1.0f, 0.0f,  0.0f, 0.0f,  0.0f, 1.0f,
    };
    private float[] normals = new float[] {
        0.0f, 0.0f, -1.0f,  0.0f, 0.0f, -1.0f,  0.0f, 0.0f, -1.0f, // back face
	    0.0f, 0.0f, -1.0f,  0.0f, 0.0f, -1.0f,  0.0f, 0.0f, -1.0f,
	    1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f, // right face
	    1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,
	    0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f, // front face
	    0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,
	    -1.0f, 0.0f, 0.0f,  -1.0f, 0.0f, 0.0f,  -1.0f, 0.0f, 0.0f, // left face
	    -1.0f, 0.0f, 0.0f,  -1.0f, 0.0f, 0.0f,  -1.0f, 0.0f, 0.0f,
	    0.0f, -1.0f, 0.0f,  0.0f, -1.0f, 0.0f,  0.0f, -1.0f, 0.0f, // bottom face
	    0.0f, -1.0f, 0.0f,  0.0f, -1.0f, 0.0f,  0.0f, -1.0f, 0.0f,
    };
    public ManualBox() {
        setNumVertices(30);
        setVertices(vertices);
        setTexCoords(texCoords);
        setNormals(normals);
        setWindingOrderCCW(false);
    }
}