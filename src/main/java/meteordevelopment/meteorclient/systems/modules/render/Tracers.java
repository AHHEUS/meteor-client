import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class TracersRenderer {

    private static final String VERTEX_SHADER_CODE = """
        #version 330 core

        layout(location = 0) in vec3 inPosition;

        uniform mat4 projectionViewMatrix;

        void main() {
            gl_Position = projectionViewMatrix * vec4(inPosition, 1.0);
        }
        """;

    private static final String FRAGMENT_SHADER_CODE = """
        #version 330 core

        out vec4 fragColor;

        uniform vec4 color;

        void main() {
            fragColor = color;
        }
        """;

    private int programId;
    private int projectionViewMatrixLocation;
    private int colorLocation;

    public void init() {
        GL.createCapabilities();

        int vertexShaderId = compileShader(GL20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShaderId = compileShader(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL20.glLinkProgram(programId);

        projectionViewMatrixLocation = GL20.glGetUniformLocation(programId, "projectionViewMatrix");
        colorLocation = GL20.glGetUniformLocation(programId, "color");

        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
    }

    public void renderTracer(double startX, double startY, double startZ, double endX, double endY, double endZ, float r, float g, float b, float a, float[] projectionViewMatrix) {
        GL20.glUseProgram(programId);

        GL20.glUniformMatrix4fv(projectionViewMatrixLocation, false, projectionViewMatrix);
        GL20.glUniform4f(colorLocation, r, g, b, a);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(startX, startY, startZ);
        GL11.glVertex3d(endX, endY, endZ);
        GL11.glEnd();

        GL20.glUseProgram(0);
    }

    private int compileShader(int type, String source) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            System.err.println("Shader compilation failed: " + GL20.glGetShaderInfoLog(shaderId));
            throw new RuntimeException("Shader compilation failed");
        }

        return shaderId;
    }
}
