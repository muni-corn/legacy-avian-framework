
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.io.*;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ModelUtils {
//
//	private static FloatBuffer reserveData(int size) {
//		return BufferUtils.createFloatBuffer(size);
//	}
//
//	private static float[] asFloats(Vector3f v) {
//		return new float[] { v.x, v.y, v.z };
//	}
//
//	private static float[] asFloats(Vector2f v) {
//		return new float[] { v.x, v.y };
//	}
//
//	public static int[] createVBO(AvianModelData model) {
//		int vboVertexHandle = glGenBuffers();
//		int vboNormalHandle = glGenBuffers();
//		int vboTexCoordHandle = glGenBuffers();
//
//		FloatBuffer vertices = reserveData(model.getFaces().size() * 9);
//		FloatBuffer normals = reserveData(model.getFaces().size() * 9);
//		FloatBuffer textureCoords = reserveData(model.getFaces().size() * 6);
//
//		for (AvianModelData.Face face : model.getFaces()) {
//			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[0] - 1)));
//			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[1] - 1)));
//			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[2] - 1)));
//			normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[0] - 1)));
//			normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[1] - 1)));
//			normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[2] - 1)));
//			if (model.hasTextureCoordinates()) {
//				textureCoords.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[0] - 1)));
//				textureCoords.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[1] - 1)));
//				textureCoords.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[2] - 1)));
//			}
//		}
//
//		vertices.flip();
//		normals.flip();
//		textureCoords.flip();
//
//		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
//		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//		glVertexPointer(3, GL_FLOAT, 0, 0L);
//
//		glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
//		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
//		glNormalPointer(GL_FLOAT, 0, 0L);
//
//		glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordHandle);
//		glBufferData(GL_ARRAY_BUFFER, textureCoords, GL_STATIC_DRAW);
//		glTexCoordPointer(2, GL_FLOAT, 0, 0L);
//
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		return new int[] { vboVertexHandle, vboNormalHandle,
//				vboTexCoordHandle };
//	}
//
//	private static Vector3f parseVertex(String line) {
//		String[] xyz = line.split(" ");
//		float x = Float.valueOf(xyz[1]);
//		float y = Float.valueOf(xyz[2]);
//		float z = Float.valueOf(xyz[3]);
//		return new Vector3f(x, y, z);
//	}
//
//	private static Vector2f parseTexCoord(String line) {
//		String[] st = line.split(" ");
//		float s = Float.valueOf(st[1]);
//		float t = 1f - Float.valueOf(st[2]);
//		return new Vector2f(s, t);
//	}
//
//	private static Vector3f parseNormal(String line) {
//		String[] xyz = line.split(" ");
//		float x = Float.valueOf(xyz[1]);
//		float y = Float.valueOf(xyz[2]);
//		float z = Float.valueOf(xyz[3]);
//		return new Vector3f(x, y, z);
//	}
//
//	private static AvianModelData.Face parseFace(boolean hasNormals, boolean hasTexCoords, String line) {
//		String[] faceIndices = line.split(" ");
//		int[] vertexIndicesArray = {
//				Integer.parseInt(faceIndices[1].split("/")[0]),
//				Integer.parseInt(faceIndices[2].split("/")[0]),
//				Integer.parseInt(faceIndices[3].split("/")[0]) };
//		if (hasNormals) {
//			int[] normalIndicesArray = new int[3];
//			normalIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[2]);
//			normalIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[2]);
//			normalIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[2]);
//			if (hasTexCoords) {
//				int[] texCoordIndicesArray = new int[3];
//				texCoordIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[1]);
//				texCoordIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[1]);
//				texCoordIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[1]);
//				return new AvianModelData.Face(vertexIndicesArray, normalIndicesArray, texCoordIndicesArray);
//			}
//			return new AvianModelData.Face(vertexIndicesArray, normalIndicesArray);
//		}
//		return new AvianModelData.Face(vertexIndicesArray);
//	}
//
//	public static AvianModelData loadModel(String f) throws IOException {
//		BufferedReader reader = new BufferedReader(new InputStreamReader(ModelUtils.class.getResourceAsStream(f)));
//		AvianModelData m = new AvianModelData();
//		String line;
//		while ((line = reader.readLine()) != null) {
//			String prefix = line.split(" ")[0];
//			if (prefix.equals("#")) {
//				continue;
//			} else if (prefix.equals("v")) {
//				m.getVertices().add(parseVertex(line));
//			} else if (prefix.equals("vn")) {
//				m.getNormals().add(parseNormal(line));
//			} else if (prefix.equals("vt")) {
//				m.getTextureCoordinates().add(parseTexCoord(line));
//			} else if (prefix.equals("f")) {
//				m.getFaces().add(parseFace(m.hasNormals(), m.hasTextureCoordinates(), line));
//			}
//		}
//		reader.close();
//		return m;
//	}

}
