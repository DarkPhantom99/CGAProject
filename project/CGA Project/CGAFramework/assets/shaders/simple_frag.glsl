#version 330 core

uniform sampler2D shadowTexture;

in vec2 TexCoords;

//fragment shader output
out vec4 color;

void main(){
    float depthValue = texture(shadowTexture, TexCoords).r;
    color = vec4(vec3(depthValue), 1.0f);
}
