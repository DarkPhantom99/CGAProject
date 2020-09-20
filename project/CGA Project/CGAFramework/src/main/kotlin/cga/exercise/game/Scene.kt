 package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.components.water.WaterFramBuffers
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader

import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.GL_TEXTURE2
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.awt.MenuShortcut
import java.nio.file.Paths

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram
    private val depthShader: ShaderProgram
    private val testShader : ShaderProgram

    var camera : TronCamera
    var sunCamera : TronCamera
    var staticCamera : TronCamera

    var world : Renderable?
    var sun : Renderable?
    var moon : Renderable?

    var pointLight : PointLight
    var spotLight : SpotLight

    var xPosition : Double = 0.0
    var yPosition : Double = 0.0

    var cam : Int = 0

    var fbo : Int = 0
    var shadowTexture : Int




    //scene setup
    init {


        //-------------------------------ObjLoader--------------------------------------------

        world = ModelLoader.loadModel("assets/models/worldfinal.obj",0f,0f,0f)


        sun = ModelLoader.loadModel("assets/models/sunv2.obj",0f,0f,0f)
        sun?.translateLocal(Vector3f(0f,0f,100f))

        moon = ModelLoader.loadModel("assets/models/moon.obj",0f,0f,0f)
        moon?.translateLocal(Vector3f(0f,0f,50f))


        //------------------------------Camera---------------------------------------------

        camera = TronCamera()
        camera.rotateLocal(0f,0f,0f)
        camera.translateLocal(Vector3f(0f,0f,40f))

        sunCamera = TronCamera()
        sunCamera.translateLocal(Vector3f(0f,0f,60f))

        staticCamera = TronCamera()
        staticCamera.rotateLocal(300f,0f,0f)
        staticCamera.translateLocal(Vector3f(0f,0f,105f))

        //------------------------------Light--------------------------------

        pointLight = PointLight(Vector3f(0f,5f,0f), Vector3f(0f,0f,0f))
        pointLight.parent = sun

        spotLight = SpotLight(Vector3f(0f,0f,0f),Vector3f(1f,1f,1f),310f,0f)
        spotLight.parent = sun

        //--------------------------------------------------------------------------------
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")

        depthShader = ShaderProgram("assets/shaders/shadow_vert.glsl", "assets/shaders/shadow_frag.glsl")

        testShader = ShaderProgram("assets/shaders/test_vert.glsl", "assets/shaders/test_frag.glsl")


        fbo = glGenFramebuffers()

        shadowTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowTexture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0 , GL_DEPTH_COMPONENT, 1024, 1024, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        GL11.glTexParameterfv(GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, floatArrayOf(1.0f,1.0f,1.0f,1.0f))


        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, shadowTexture, 0)
        GL11.glDrawBuffer(GL_NONE)
        GL11.glReadBuffer(GL_NONE)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)



        //initial opengl state
        //glClearColor(0.6f, 1.0f, 1.0f, 1.0f); GLError.checkThrow() Blau
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        //glDisable(GL_CULL_FACE); GLError.checkThrow()

        GL11.glDisable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()

        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

    }

    fun render(dt: Float, t: Float) {

        // Render Depth of View to Texture(Lights Perspective)
        var lightView : Matrix4f = Matrix4f()
        var lightProjection : Matrix4f = Matrix4f()

        lightView =  lightView.lookAt(spotLight.getWorldPosition(),Vector3f(0.0f), Vector3f(0.0f, 1.0f, 0.0f))
        lightProjection = lightProjection.ortho(-20f,20f,-20f,20f,0.1f,100f)

        depthShader.use()
        depthShader.setUniform("lightView",lightView, false)
        depthShader.setUniform("lightProjection",lightProjection,false)

        GL11.glViewport(0,0,1024,1024)
        glBindFramebuffer(GL_FRAMEBUFFER,fbo)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        world?.render(depthShader)
        moon?.render(depthShader)

        glBindFramebuffer(GL_FRAMEBUFFER,0)


        //Render Depth map to Scene
        GL11.glViewport(0, 0, window.windowWidth, window.windowHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        testShader.use()
        testShader.setUniform("lightView",lightView, false)
        testShader.setUniform("lightProjection",lightProjection,false)
        if (cam == 0){
            camera.bind(testShader)
            spotLight.bind(testShader,"asd", camera.getCalculateViewMatrix())
            pointLight.bind(testShader,"s")
        }
        else if(cam == 1) {
            sunCamera.bind(testShader)
            spotLight.bind(testShader,"asd", sunCamera.getCalculateViewMatrix())
            pointLight.bind(testShader,"s")
        }
        else{
            staticCamera.bind(testShader)
            spotLight.bind(testShader,"asd", staticCamera.getCalculateViewMatrix())
            pointLight.bind(testShader,"s")
        }

        GL13.glActiveTexture(GL_TEXTURE5)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowTexture)
        testShader.setUniform("shadowTexture",5)
        sun?.render(testShader)
        moon?.render(testShader)
        world?.render(testShader)


    }

    fun update(dt: Float, t: Float) {

        // Camera 1 Transformations
        if (window.getKeyState(GLFW_KEY_W)){
            camera.translateLocal(Vector3f(0f,0f,-0.1f))
            if (window.getKeyState(GLFW_KEY_A)){
                camera.translateLocal(Vector3f(-0.1f,0f,0f))
            }
            if (window.getKeyState(GLFW_KEY_D)){
                camera.translateLocal(Vector3f(0.1f,0f,0f))
            }
        }
        else if (window.getKeyState(GLFW_KEY_S)){
            camera.translateLocal(Vector3f(0f,0f,0.1f))
            if (window.getKeyState(GLFW_KEY_A)){
                camera.translateLocal(Vector3f(-0.1f,0f,0f))
            }
            if (window.getKeyState(GLFW_KEY_D)){
                camera.translateLocal(Vector3f(0.1f,0f,0f))
            }
        }
        else if (window.getKeyState(GLFW_KEY_A)){
            camera.translateLocal(Vector3f(-0.1f,0f,0f))
        }
       else if (window.getKeyState(GLFW_KEY_D)){
            camera.translateLocal(Vector3f(0.1f,0f,0f))
        }

        //  Camera Switch
        if (window.getKeyState(GLFW_KEY_1)){
           cam = 0
        }
        if (window.getKeyState(GLFW_KEY_2)){
            cam = 1
        }
        if (window.getKeyState(GLFW_KEY_3)){
            cam = 2
        }

        world?.rotateLocal(-0.0003f,-0.0004f,0f)
        sun?.rotateAroundPoint(0f,0.002f,0f, Vector3f(0f,0f,0f))
        sunCamera.rotateAroundPoint(0f,0.002f,0f, Vector3f(0f,0f,0f))
        moon?.rotateAroundPoint(0f,-0.002f,0f, Vector3f(0f,0f,0f))
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        sunCamera.rotateLocal(Math.toRadians((-ypos + yPosition ).toFloat() * 0.02f), Math.toRadians((-xpos + xPosition ).toFloat() * 0.02f),0f )
        camera.rotateLocal(Math.toRadians((-ypos + yPosition ).toFloat() * 0.02f), Math.toRadians((-xpos + xPosition ).toFloat() * 0.02f),0f )
        xPosition = xpos
        yPosition = ypos
    }

    fun cleanup() {}
}
