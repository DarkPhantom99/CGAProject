package cga.exercise.components.water

import cga.exercise.game.Game
import cga.framework.GameWindow
import org.lwjgl.opengl.ARBFramebufferObject.*
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32
import org.lwjgl.opengl.GL32
import java.awt.DisplayMode
import java.nio.ByteBuffer

abstract class WaterFramBuffers {

    private val REFLACTION_WIDTH : Int = 320
    private val REFLACTION_HEIGHT : Int = 180

    private val REFRACTION_WIDTH : Int = 1280
    private val REFRACTION_HEIGTH : Int = 720

    private var reflectionFramBuffer : Int = 0
    private var reflactionTexture : Int  = 0
    private var reflactionDepthTexture : Int = 0

    private var refractionFramBuffer : Int = 0
    private var refractionTexture : Int = 0
    private  var refractionDepthTexture : Int = 0

    fun WaterFramBuffers(){
        initialiseReflactionFramBuffer()
        initialiseRefractionFramBuffer()
    }

    fun clean(){
        glDeleteFramebuffers(reflectionFramBuffer)
        glDeleteTextures(reflactionTexture)
        glDeleteRenderbuffers(reflactionDepthTexture)
        glDeleteFramebuffers(refractionFramBuffer)
        glDeleteTextures(refractionTexture)
        glDeleteRenderbuffers(refractionDepthTexture)
    }

    fun binReflactionFrameBuffer() {
        bindFramBuffer(reflactionTexture, REFLACTION_WIDTH, REFLACTION_HEIGHT)
    }

    fun bindRefractionFramBuffer(){
        bindFramBuffer(refractionFramBuffer, REFRACTION_WIDTH, REFRACTION_HEIGTH)
    }

    fun unbindCurrentFramBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        //glViewport(0,0,)
    }

    fun gerReflactionTexture() : Int{
        return reflactionTexture
    }

    fun getRefractionTexture() : Int {
        return refractionTexture
    }

    fun getFefractionDepthTexture() : Int{
        return refractionDepthTexture
    }

    fun initialiseReflactionFramBuffer(){
        reflectionFramBuffer = createFrameBuffer()
        reflactionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGTH)
        reflactionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGTH)
        unbindCurrentFramBuffer()
    }

    fun initialiseRefractionFramBuffer(){
        refractionFramBuffer = createFrameBuffer()
        refractionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGTH)
        refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGTH)
        unbindCurrentFramBuffer()
    }

    fun bindFramBuffer(framBuffer : Int, width: Int, height: Int){
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, framBuffer)
        //glViewport(0,0,width,height)
    }

    fun createFrameBuffer() : Int{
        var frameBuffer : Int = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER,frameBuffer)
        GL11.glDrawBuffer(GL_COLOR_ATTACHMENT0)
        return frameBuffer
    }

    fun createTextureAttachment( width : Int, height : Int ) : Int{
        var Empty : ByteBuffer? = null
        var texture : Int = GL11.glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE,Empty)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0)
        return texture
    }

    fun createDepthTextureAttachment(width: Int, height: Int) : Int{
        var Empty : ByteBuffer? = null
        var texture : Int = GL11.glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE,Empty)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0)
        return texture
    }

    fun createDepthBufferAttachment (width: Int, height: Int) : Int {
        var depthBuffer : Int = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer)
        return depthBuffer
    }

}