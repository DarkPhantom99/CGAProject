#version 330 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 texcoords;

//uniforms
//translation object to world
uniform mat4 model_matrix;
uniform mat4 view;
uniform mat4 projection;
uniform vec2 tcMultiplier;
uniform vec3 pointLightPos;
uniform vec3 spotLightPos;



out struct VertexData
{
    vec3 viewDir;
    vec3 lightDir;
    vec3 lightDirSP;
    vec3 norm;
    vec2 texcoords;

} vertexData;

const vec4 plane = vec4(0, -1.0, 0, 15);
void main(){

    vec4 pos = projection * view * model_matrix * vec4(position, 1.0f);
    gl_Position = pos;
    vertexData.texcoords = tcMultiplier * texcoords;

    //vertexData.fragPos = vec3(model_matrix * vec4(position, 1.0f));

    //vec4 n = vec4(normal, 0.0f);
    //mat4 normalMat = transpose(inverse(view*model_matrix));


    vertexData.norm = mat3(transpose(inverse(view*model_matrix))) * normal;


    //LightDir
    vec4 lp = view * vec4(pointLightPos, 1.0f);
    vec4 p = (view * model_matrix * vec4(position, 1.0f));
    vertexData.lightDir = (lp - p).xyz;

    //SpotLightDir
    vertexData.lightDirSP =  spotLightPos - p.xyz ;

    //ViewDir
    vertexData.viewDir = -p.xyz;


}
