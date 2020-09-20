#version 330 core

uniform sampler2D emit;
uniform sampler2D spec;
uniform sampler2D diff;
uniform sampler2D shadowTexture;
uniform vec3 pointLightColor;
uniform vec3 pointLightAttenuation;
uniform vec3 spotLightColor;
uniform vec3 spotLightAttenuation;
uniform float innerCutOff;
uniform float outerCutOff;
uniform float shininess;
uniform vec3 spotLightDir;
uniform vec3 emitColor;
/*
float shadowCalculation(vec4 fragPosLightSpace){
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;

    float closestDepth = texture(shadowTexture, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float shadow = currentDepth > closestDepth ? 1.0 : 0.0;
    return shadow;
}*/
/*
float shadowCalculation(vec4 fragPosLightSpace, float dotLightNormal){
    vec3 pos = fragPosLightSpace.xyz *0.5 + 0.5;
    if(pos.z >1.0 ){
        pos.z = 1.0;
    }
    float depth = texture(shadowTexture,pos.xy).r;
    float bias = max(0.05 * (1.0 - dotLightNormal), 0.005);
    return (depth + bias) < pos.z ? 0.0 : 1.0;
    //return depth < pos.z ? 0.0 : 1.0;

}*/

float shadowCalculation(vec4 fragPosLightSpace, float dotLightNormal){
    vec3 pos = fragPosLightSpace.xyz *0.5 + 0.5;
    if(pos.z >1.0 ){
        pos.z = 1.0;
    }

    float bias = max(0.05 * (1.0 - dotLightNormal), 0.005);

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowTexture, 0);
    for(int x = -1; x <= 1 ; ++x){
        for (int y = -1; y <= 1 ; ++y){
            float depth = texture(shadowTexture, pos.xy + vec2(x,y) * texelSize).r;
            shadow += (depth + bias) < pos.z ? 0.0 : 1.0;
        }
    }
    return shadow/9.0;
    //return depth < pos.z ? 0.0 : 1.0;

}


// Ambient
vec3 calcAmbient(vec3 difftex , vec3 colorAmbient){
    vec3 ambient = difftex * colorAmbient;
    return ambient;
}


//(Diffuse + Specular)
vec3 calcDiffSpec(vec3 normal, vec3 lightDir,vec3 viewDir, vec3 difftex, vec3 spectex, float shininess ){

    float cosa = max(dot(normalize(normal),normalize(lightDir)),0.0f);
    vec3 diffuse = cosa * difftex;

    vec3 reflect = reflect(normalize(-lightDir), normalize(normal));
    float cosBeta = max(0.0f, dot(normalize(viewDir), reflect));
    float cosBetaK = pow(cosBeta, shininess);
    vec3 spec = spectex * cosBetaK;

    return diffuse + spec;
}



//Attenuatoion
float calcAttenuation(float distance, vec3 attenuationValues ){
    float attenuation = 1.0f/(attenuationValues.x + attenuationValues.y * distance + attenuationValues.z * (distance * distance));
    return attenuation;

}


//Intensity
float calcIntensity(vec3 lightDir , vec3 slightDir, float outerCutOff, float innerCutOff ){
    float theta = dot(normalize(lightDir), normalize(-slightDir));
    float intensity = clamp((theta - outerCutOff) / (innerCutOff - outerCutOff) , 0.0f, 1.0f);
    return intensity;
}



//input from vertex shader
in struct VertexData
{
    vec3 viewDir;
    vec3 lightDir;
    vec3 lightDirSP;//
    vec3 norm;
    vec2 texcoords;
        vec4 fragPosLightSpace;

} vertexData;


out vec4 color;

//fragment shader output
void main(){

    float shadow = 1.0f;

    vec3 pos2 = vertexData.fragPosLightSpace.xyz *0.5 + 0.5;

    float stex = texture(shadowTexture,pos2.xy).r;
    vec3 emittex = texture(emit, vertexData.texcoords).rgb;
    vec3 spectex = texture(spec,vertexData.texcoords).rgb;
    vec3 difftex = texture(diff, vertexData.texcoords).rgb;

    //Ambient
    vec3 ambient = calcAmbient(difftex, vec3(0,1,0)*0.04);

    //Diffuse + Specular

    vec3 diffSpecPL = calcDiffSpec(vertexData.norm, vertexData.lightDir,vertexData.viewDir,  difftex,  spectex, shininess );

    vec3 diffSpecSL = calcDiffSpec(vertexData.norm, vertexData.lightDirSP, vertexData.viewDir, difftex, spectex, shininess);

    //Intensity
    float intensitySL = calcIntensity(spotLightDir,vertexData.lightDirSP, outerCutOff, innerCutOff);


    //Attenuation
    float attenuationPL = calcAttenuation(length(vertexData.lightDir), pointLightAttenuation);
    //diffSpecPL *= attenuationPL;

    float atteunationSL = calcAttenuation(length(vertexData.lightDirSP), spotLightAttenuation);
    //diffSpecSL *= atteunationSL;

    vec3 spotLightCol = shadow*intensitySL * atteunationSL * spotLightColor*10;
    vec3 pointLightCol =shadow * attenuationPL * pointLightColor;


    float dotLightNormal = dot(normalize(vertexData.lightDirSP), vertexData.norm);
    float shadoww = shadowCalculation(vertexData.fragPosLightSpace, dotLightNormal);
    //result
    //vec3 resultSL = diffSpecSL * emittex;
    //vec3 resultPL = (ambientEmitPl+diffSpecPL)*emittex;
    vec3 result = (emittex + ambient +  (shadoww *( diffSpecPL*pointLightCol + diffSpecSL*spotLightCol )));
    //vec3 result = vec3(diffSpecPL*pointLightCol);
    color = vec4(result,1.0f);
    //color = vec4(vec3(stex),1.0f);
}

