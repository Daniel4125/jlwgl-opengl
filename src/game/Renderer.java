package game;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryUtil.memFree;

import engine.Utils;
import engine.Window;
import engine.graph.ShaderProgram;
import org.lwjgl.system.MemoryUtil;

public class Renderer {

    private int vboId; // Vertex Buffer Object
    private int vaoId; // Vertex Array Object
    private ShaderProgram shaderProgram;

    public Renderer() {}

    public void init() throws Exception {
        this.shaderProgram =  new ShaderProgram();
        this.shaderProgram.createVertexShader(Utils.loadResource("/resources/vertex.vs"));
        this.shaderProgram.createFragmentShader(Utils.loadResource("/resources/fragment.fs"));
        shaderProgram.link();

        // array of floats describing the
        // vertices of a triangle
        float[] vertices = new float[]{
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
        };

        // Store array of floats into a float buffer in order to interface
        // with C-based opengl
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();

        // Create the VAO and bind to it
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // create the VBO, bind it and put the data into it.
        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        //memFree(verticesBuffer);

        // Define structure of the data, store it in one of the attribute lists
        // of the VAO.
        // -> index=0 (index where shader expects data)
        // -> size=3 (no. of components per vertex attribute(1 to 4). 3D coords -> size=3
        // -> normalised=false
        // -> stride=0 (byte offset between consecutive generic vertex attributes)
        // -> offset=0 (Specifies an offset to the first component in the buffer)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // unbind the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Unbind the VAO
        glBindVertexArray(0);

        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer);
        }

    }

    public void render(Window window) {
        clear();

        if (window.isResized()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        this.shaderProgram.bind();

        // Bind to the VAO
        glBindVertexArray(this.vaoId);
        glEnableVertexAttribArray(0);

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, 3);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
        glDeleteVertexArrays(vaoId);
    }
}
