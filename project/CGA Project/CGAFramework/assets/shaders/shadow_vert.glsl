#version 330 core

layout(location = 0) in vec3 position;


uniform mat4 lightView;
uniform mat4 lightProjection;
uniform mat4 model_matrix;

//out vec2 texCoords;

void main() {
    gl_Position = lightProjection * lightView * model_matrix * vec4(position, 1.0);
    //texCoords = texcoords;
}
