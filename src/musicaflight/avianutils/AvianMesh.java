
package musicaflight.avianutils;

public class AvianMesh {

	//	public int vboVertexHandle, vboNormalHandle, vboColorHandle,
	//			vboTexCoordHandle;
	//
	//	private AvianModelData model;
	//
	//	public float x, y, z, w, h, d, pitch, yaw, roll;
	//
	//	private String modelFilepath;
	//
	//	public AvianMesh(String filepath) {
	//		modelFilepath = filepath;
	//		w = 1;
	//		h = 1;
	//		d = 1;
	//		setUpVBO();
	//	}
	//
	//	public AvianMesh(String filepath, float x, float y, float z) {
	//		this(filepath);
	//		this.x = x;
	//		this.y = y;
	//		this.z = z;
	//	}
	//
	//	public AvianMesh(String filepath, float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
	//		this(filepath, x, y, z);
	//		w = scaleX;
	//		h = scaleY;
	//		d = scaleZ;
	//	}
	//
	//	public AvianMesh(String filepath, float x, float y, float z, float scaleX, float scaleY, float scaleZ, float pitch, float yaw, float roll) {
	//		this(filepath, x, y, z, scaleX, scaleY, scaleZ);
	//		this.pitch = pitch;
	//		this.yaw = yaw;
	//		this.roll = roll;
	//	}
	//
	//	private FloatBuffer colorData;
	//
	//	private void setUpVBO() {
	//		try {
	//			int[] vbos = ModelUtils.createVBO(model = ModelUtils.loadModel(modelFilepath));
	//			vboVertexHandle = vbos[0];
	//			vboNormalHandle = vbos[1];
	//			vboTexCoordHandle = vbos[2];
	//			vboColorHandle = glGenBuffers();
	//			vbos = null;
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//			AvianApp.close();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//			AvianApp.close();
	//		}
	//
	//		colorData = BufferUtils.createFloatBuffer(model.getFaces().size() * 12);
	//
	//	}
	//
	//	private int shader;
	//
	//	public void setShader(int shader) {
	//		this.shader = shader;
	//	}
	//
	//	public void render(AvianColor avianColor) {
	//		if (shader != 0) {
	//			glUseProgram(shader);
	//
	//			glMaterialfv(GL_FRONT, GL_DIFFUSE, AvianUtils.asFlippedFloatBuffer(avianColor.getR() / 255f, avianColor.getG() / 255f, avianColor.getB() / 255f, avianColor.getA() / 255f));
	//			glMaterialf(GL_FRONT, GL_SHININESS, 128f);
	//		} else {
	//			colorData.clear();
	//			for (int i = 0; i < (model.getFaces().size() * 3); i++) {
	//				colorData.put(avianColor.getR() / 255f);
	//				colorData.put(avianColor.getG() / 255f);
	//				colorData.put(avianColor.getB() / 255f);
	//				colorData.put(avianColor.getA() / 255f);
	//			}
	//			colorData.flip();
	//		}
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
	//		glVertexPointer(3, GL_FLOAT, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
	//		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
	//		glColorPointer(4, GL_FLOAT, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, 0);
	//
	//		boolean transformed = (x != 0) || (y != 0) || (z != 0) || (w != 1) || (h != 1) || (d != 1) || (pitch != 0) || (yaw != 0) || (roll != 0);
	//
	//		if (transformed) {
	//			glPushMatrix();
	//
	//			glTranslatef(x, y, z);
	//			glRotatef(pitch, 1, 0, 0);
	//			glRotatef(yaw, 0, 1, 0);
	//			glRotatef(roll, 0, 0, 1);
	//			glScalef(w, h, d);
	//		}
	//
	//		if (glGetBoolean(GL_VERTEX_ARRAY)==GL_FALSE)
	//			glEnableClientState(GL_VERTEX_ARRAY);
	//		if (glGetBoolean(GL_COLOR_ARRAY)==GL_FALSE)
	//			glEnableClientState(GL_COLOR_ARRAY);
	//		if (glGetBoolean(GL_NORMAL_ARRAY)==GL_FALSE)
	//			glEnableClientState(GL_NORMAL_ARRAY);
	//
	//		glDrawArrays(GL_TRIANGLES, 0, model.getFaces().size() * 3);
	//
	//		if (transformed) {
	//			glPopMatrix();
	//		}
	//
	//		glUseProgram(0);
	//
	//	}
	//
	//	public void render(AvianImage img) {
	//		glEnable(GL_TEXTURE_2D);
	//
	//		if (shader != 0)
	//			glUseProgram(shader);
	//
	//		colorData.clear();
	//		for (int i = 0; i < (model.getFaces().size() * 12); i++) {
	//			colorData.put(1f);
	//		}
	//		colorData.flip();
	//
	//		if (shader != 0) {
	//			glMaterialfv(GL_FRONT, GL_DIFFUSE, colorData);
	//			glMaterialf(GL_FRONT, GL_SHININESS, 120f);
	//		}
	//		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
	//		glVertexPointer(3, GL_FLOAT, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
	//		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
	//		glColorPointer(4, GL_FLOAT, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordHandle);
	//		glTexCoordPointer(2, GL_FLOAT, 0, 0);
	//
	//		glBindBuffer(GL_ARRAY_BUFFER, 0);
	//
	//		boolean transformed = (x != 0) || (y != 0) || (z != 0) || (w != 1) || (h != 1) || (d != 1) || (pitch != 0) || (yaw != 0) || (roll != 0);
	//
	//		if (transformed) {
	//			glPushMatrix();
	//
	//			glTranslatef(x, y, z);
	//			glRotatef(pitch, 1, 0, 0);
	//			glRotatef(yaw, 0, 1, 0);
	//			glRotatef(roll, 0, 0, 1);
	//			glScalef(w, h, d);
	//		}
	//
	//		if (!glGetBoolean(GL_VERTEX_ARRAY))
	//			glEnableClientState(GL_VERTEX_ARRAY);
	//		if (!glGetBoolean(GL_COLOR_ARRAY))
	//			glEnableClientState(GL_COLOR_ARRAY);
	//		if (!glGetBoolean(GL_NORMAL_ARRAY))
	//			glEnableClientState(GL_NORMAL_ARRAY);
	//		if (!glGetBoolean(GL_TEXTURE_COORD_ARRAY))
	//			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	//
	//		glBindTexture(GL_TEXTURE_2D, img.getTextureID());
	//		glDrawArrays(GL_TRIANGLES, 0, model.getFaces().size() * 3);
	//
	//		if (transformed) {
	//			glPopMatrix();
	//		}
	//
	//		if (shader != 0)
	//			glUseProgram(0);
	//		glDisable(GL_TEXTURE_2D);
	//	}
	//
	//	public float getX() {
	//		return x;
	//	}
	//
	//	public float getLeftX() {
	//		return x - (w / 2f);
	//	}
	//
	//	public float getRightX() {
	//		return x + (w / 2f);
	//	}
	//
	//	public void setX(float x) {
	//		this.x = x;
	//	}
	//
	//	public float getY() {
	//		return y;
	//	}
	//
	//	public float getTopY() {
	//		return y + (h / 2f);
	//	}
	//
	//	public float getBottomY() {
	//		return y - (h / 2f);
	//	}
	//
	//	public void setY(float y) {
	//		this.y = y;
	//	}
	//
	//	public float getZ() {
	//		return z;
	//	}
	//
	//	public float getarZ() {
	//		return z + (d / 2f);
	//	}
	//
	//	public float getNearZ() {
	//		return z - (d / 2f);
	//	}
	//
	//	public void setZ(float z) {
	//		this.z = z;
	//	}
	//
	//	public float getW() {
	//		return w;
	//	}
	//
	//	public void setW(float w) {
	//		this.w = w;
	//	}
	//
	//	public float getH() {
	//		return h;
	//	}
	//
	//	public void setH(float h) {
	//		this.h = h;
	//	}
	//
	//	public float getD() {
	//		return d;
	//	}
	//
	//	public void setD(float d) {
	//		this.d = d;
	//	}
	//
	//	public float getPitch() {
	//		return pitch;
	//	}
	//
	//	public void setPitch(float pitch) {
	//		this.pitch = pitch;
	//	}
	//
	//	public float getYaw() {
	//		return yaw;
	//	}
	//
	//	public void setYaw(float yaw) {
	//		this.yaw = yaw;
	//	}
	//
	//	public float getRoll() {
	//		return roll;
	//	}
	//
	//	public void setRoll(float roll) {
	//		this.roll = roll;
	//	}
	//
	//	public void set(float x, float y, float z, float w, float h, float d) {
	//		this.x = x;
	//		this.y = y;
	//		this.z = z;
	//		this.w = w;
	//		this.h = h;
	//		this.d = d;
	//	}
	//
	//	public void setXYZ(float x, float y, float z) {
	//		this.x = x;
	//		this.y = y;
	//		this.z = z;
	//	}
	//
	//	public void setXYZPYR(float x, float y, float z, float pit, float yaw, float rol) {
	//		this.x = x;
	//		this.y = y;
	//		this.z = z;
	//		this.pitch = pit;
	//		this.yaw = yaw;
	//		this.roll = rol;
	//
	//	}
	//
	//	public void setXYWH(float x, float y, float w, float h) {
	//		this.x = x;
	//		this.y = y;
	//		this.w = w;
	//		this.h = h;
	//	}

}
