package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.*

class SpotLight(lightpos : Vector3f,  lightColor : Vector3f, var outerAngle : Float, var innerAngle : Float) : PointLight(lightpos, lightColor), ISpotLight {



    init {
        attenuation = Vector3f(0.5f,0.05f,0.0f)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {

        var pos = Vector4f(getWorldPosition(),1.0f).mul(viewMatrix)
        shaderProgram.setUniform("spotLightColor",lightColor)
        shaderProgram.setUniform("spotLightPos", Vector3f(pos.x,pos.y,pos.z))
        shaderProgram.setUniform("spotLightDir",getWorldZAxis().negate().mul(Matrix3f(viewMatrix)))
        shaderProgram.setUniform("outerCutOff",Math.cos(Math.toRadians(outerAngle)))
        shaderProgram.setUniform("innerCutOff",Math.cos(Math.toRadians(innerAngle)))
        shaderProgram.setUniform("spotLightAttenuation",attenuation)
    }
}