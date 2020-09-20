#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texcoords;

out vec2 TexCoords;

uniform mat4 lightView;
uniform mat4 lightProjection;
uniform mat4 model_matrix;


void main(){
    TexCoords = texcoords;
    gl_Position = lightProjection *  lightView*model_matrix * vec4(position, 1.0f);

}
