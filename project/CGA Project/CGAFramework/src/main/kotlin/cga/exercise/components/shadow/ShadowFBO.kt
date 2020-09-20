package cga.exercise.components.shadow

import cga.exercise.components.geometry.Renderable
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.glFramebufferTexture


class ShadowFBO {

    var fbo : Int = 0
    var depthTexture : Int = 0

    fun render(){
        fbo = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER,fbo)

        depthTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0 , GL_DEPTH_COMPONENT, 1024, 1024, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)

        glBindFramebuffer(GL_FRAMEBUFFER, depthTexture)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0)
        GL11.glDrawBuffer(GL_NONE)
        GL11.glReadBuffer(GL_NONE)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    /*

        shadowTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowTexture)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0 , GL30.GL_DEPTH_COMPONENT, window.windowWidth, window.windowHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,0)

        fbo = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, shadowTexture, 0)
        GL11.glDrawBuffer(GL30.GL_NONE)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
*/
    }

    fun print(){
        println("aaaaaaaaaaaaa")
    }



}