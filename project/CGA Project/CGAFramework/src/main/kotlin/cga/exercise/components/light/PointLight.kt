package cga.exercise.components.light

import cga.exercise.components.geometry.ITransformable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

open class PointLight(lightpos : Vector3f, var lightColor : Vector3f) : Transformable(),IPointLight {

    var attenuation = Vector3f(1.0f, 0.5f, 0.1f)

   init {
       translateGlobal(lightpos)
   }

    override fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.setUniform("pointLightPos",getWorldPosition())
        shaderProgram.setUniform("pointLightColor", lightColor)
        shaderProgram.setUniform("pointLightAttenuation",attenuation)
    }

}