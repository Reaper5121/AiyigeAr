package com.aiyige.customer.compose.easyar;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.easyar.Buffer;
import cn.easyar.CameraParameters;
import cn.easyar.Engine;
import cn.easyar.Image;
import cn.easyar.InputFrame;
import cn.easyar.MotionTrackerCameraDevice;
import cn.easyar.MotionTrackerCameraDeviceFPS;
import cn.easyar.MotionTrackerCameraDeviceFocusMode;
import cn.easyar.MotionTrackerCameraDeviceResolution;
import cn.easyar.MotionTrackerCameraDeviceTrackingMode;
import cn.easyar.MotionTrackingStatus;
import cn.easyar.OutputFrame;
import cn.easyar.OutputFrameBuffer;
import cn.easyar.PlaneData;
import cn.easyar.PlaneType;
import cn.easyar.SparseSpatialMap;
import cn.easyar.Vec2F;
import cn.easyar.Vec3F;

/** EasyAR camera, 6DoF tracking, plane mapping, and metric panel placement. */
public final class EasyArPanelSurfaceView extends GLSurfaceView {
    public interface StatusListener { void onStatus(String message); }

    private final SceneRenderer sceneRenderer;
    private volatile float readyWidthMeters = 2.4f;
    private volatile float readyHeightMeters = 0.2f;
    private volatile Bitmap pendingBitmap;
    private volatile StatusListener statusListener;
    private float previousPinchDistance;

    public EasyArPanelSurfaceView(Context context, StatusListener listener) {
        super(context);
        statusListener = listener;
        setEGLContextClientVersion(3);
        setPreserveEGLContextOnPause(true);
        sceneRenderer = new SceneRenderer(context);
        setRenderer(sceneRenderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setOnTouchListener((view, event) -> {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                final float x = event.getX();
                final float y = event.getY();
                queueEvent(() -> sceneRenderer.beginPointer(x, y, getWidth(), getHeight()));
                return true;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() >= 2) {
                    float distance = pointerDistance(event);
                    if (previousPinchDistance > 0f && distance > 0f) {
                        float factor = distance / previousPinchDistance;
                        if (Math.abs(factor - 1f) > 0.005f) {
                            queueEvent(() -> sceneRenderer.resizeSelected(factor));
                        }
                    }
                    previousPinchDistance = distance;
                    return true;
                }
                final float x = event.getX();
                final float y = event.getY();
                queueEvent(() -> sceneRenderer.movePointer(x, y, getWidth(), getHeight()));
                return true;
            }
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                previousPinchDistance = pointerDistance(event);
                return true;
            }
            if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
                previousPinchDistance = 0f;
                queueEvent(sceneRenderer::cancelPointer);
                return true;
            }
            if (action == MotionEvent.ACTION_UP) {
                previousPinchDistance = 0f;
                final float x = event.getX();
                final float y = event.getY();
                queueEvent(() -> sceneRenderer.endPointer(x, y, getWidth(), getHeight()));
                performClick();
                return true;
            }
            return true;
        });
    }

    private float pointerDistance(MotionEvent event) {
        if (event.getPointerCount() < 2) return 0f;
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override public boolean performClick() { super.performClick(); return true; }

    public void setPanelSize(float widthMeters, float heightMeters) {
        readyWidthMeters = Math.max(0.02f, widthMeters);
        readyHeightMeters = Math.max(0.02f, heightMeters);
        queueEvent(() -> sceneRenderer.setReadySize(readyWidthMeters, readyHeightMeters));
    }

    public void setPanelBitmap(Bitmap bitmap) {
        pendingBitmap = bitmap;
        queueEvent(() -> sceneRenderer.setBitmap(pendingBitmap));
    }

    public void requestPlacement(float x, float y) {
        queueEvent(() -> sceneRenderer.placePanel(x, y, getWidth(), getHeight()));
    }

    public void resizeSelected(float factor) { queueEvent(() -> sceneRenderer.resizeSelected(factor)); }
    public void deleteSelected() { queueEvent(sceneRenderer::deleteSelected); }
    public void clearPanels() { queueEvent(sceneRenderer::clearPanels); }
    public void setStatusListener(StatusListener listener) { statusListener = listener; }

    @Override public void onResume() {
        super.onResume();
        Engine.onResume();
        queueEvent(sceneRenderer::startCameraIfReady);
    }
    @Override public void onPause() {
        queueEvent(sceneRenderer::stopCamera);
        Engine.onPause();
        super.onPause();
    }

    private void reportStatus(String message) {
        StatusListener listener = statusListener;
        if (listener != null) post(() -> listener.onStatus(message));
    }

    private final class SceneRenderer implements GLSurfaceView.Renderer {
        private final Context context;
        private final ArrayList<PanelPlacement> panels = new ArrayList<>();
        private final AtomicReference<Bitmap> bitmapToUpload = new AtomicReference<>();
        private EasyArBackgroundRenderer background;
        private MotionTrackerCameraDevice camera;
        private SparseSpatialMap spatialMap;
        private OutputFrameBuffer outputFrames;
        private int viewportWidth = 1;
        private int viewportHeight = 1;
        private int frameNumber;
        private int previousImageFrame = -1;
        private byte[] imagePixels;
        private int panelProgram;
        private int panelTexture;
        private boolean cameraStarted;
        private int selectedPanel = -1;
        private float panelWidth = 2.4f;
        private float panelHeight = 0.2f;
        private long lastRenderNanos;
        private float[] projectionGl;
        private float[] viewGl;
        private Vec2F latestImageTap;
        private boolean pointerStartedOnPanel;
        private String lastStatus = "";

        SceneRenderer(Context context) { this.context = context.getApplicationContext(); }

        @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES30.glClearColor(0f, 0f, 0f, 1f);
            background = new EasyArBackgroundRenderer();
            createPanelProgram();
            try {
                if (!MotionTrackerCameraDevice.isAvailable()) {
                    reportIfChanged("EasyAR motion tracking is unavailable on this device.");
                    return;
                }
                outputFrames = OutputFrameBuffer.create();
                spatialMap = SparseSpatialMap.create();
                spatialMap.setResultPoseType(true);
                camera = new MotionTrackerCameraDevice();
                camera.setFrameRateType(MotionTrackerCameraDeviceFPS.Camera_FPS_30);
                camera.setFocusMode(MotionTrackerCameraDeviceFocusMode.Continousauto);
                camera.setFrameResolutionType(MotionTrackerCameraDeviceResolution.Resolution_640);
                camera.setTrackingMode(MotionTrackerCameraDeviceTrackingMode.SLAM);
                camera.setBufferCapacity(outputFrames.bufferRequirement() + spatialMap.bufferRequirement() + 3);
                camera.inputFrameSource().connect(spatialMap.inputFrameSink());
                spatialMap.outputFrameSource().connect(outputFrames.input());
                if (!spatialMap.start()) {
                    reportIfChanged("EasyAR could not start spatial mapping. Check the EasyAR license.");
                    return;
                }
                startCameraIfReady();
                if (!cameraStarted) {
                    reportIfChanged("EasyAR could not start the camera tracker. Check camera permission and license.");
                    return;
                }
                reportIfChanged("Scan slowly across the room to detect walls and ceilings.");
            } catch (RuntimeException error) {
                reportIfChanged("EasyAR startup failed: " + error.getMessage());
            }
        }

        void startCameraIfReady() {
            if (camera != null && !cameraStarted) cameraStarted = camera.start();
        }

        void stopCamera() {
            if (camera != null && cameraStarted) {
                camera.stop();
                cameraStarted = false;
            }
        }

        @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
            viewportWidth = Math.max(1, width);
            viewportHeight = Math.max(1, height);
        }

        @Override public void onDrawFrame(GL10 gl) {
            long now = System.nanoTime();
            if (now - lastRenderNanos < 50_000_000L) return;
            lastRenderNanos = now;
            GLES30.glViewport(0, 0, viewportWidth, viewportHeight);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
            if (camera == null || outputFrames == null) return;
            updateBitmapTexture();
            OutputFrame output = outputFrames.peek();
            if (output == null) return;
            InputFrame input = output.inputFrame();
            if (input == null) { output.dispose(); return; }
            Image image = input.image();
            CameraParameters cameraParameters = input.cameraParameters();
            if (image == null || cameraParameters == null) {
                if (image != null) image.dispose();
                if (cameraParameters != null) cameraParameters.dispose();
                input.dispose(); output.dispose();
                return;
            }
            try {
                if (input.index() != previousImageFrame) {
                    Buffer buffer = image.buffer();
                    try {
                        if (imagePixels == null || imagePixels.length != buffer.size()) imagePixels = new byte[buffer.size()];
                        buffer.copyToByteArray(imagePixels);
                        background.upload(image.format(), image.width(), image.height(), image.pixelWidth(), image.pixelHeight(), ByteBuffer.wrap(imagePixels));
                    } finally { buffer.dispose(); }
                    previousImageFrame = input.index();
                }
                int rotation = screenRotation();
                float aspect = (float) viewportWidth / viewportHeight;
                GLES30.glDisable(GLES30.GL_DEPTH_TEST);
                background.render(cameraParameters.imageProjection(aspect, rotation, true, false));
                projectionGl = toGl(cameraParameters.projection(0.01f, 100f, aspect, rotation, true, false).data);
                viewGl = new float[16];
                Matrix.invertM(viewGl, 0, toGl(input.cameraTransform().data), 0);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDisable(GLES30.GL_CULL_FACE);
                List<PlaneData> detectedPlanes = spatialMap.getMapPlanes();
                drawDetectedPlanes(detectedPlanes);
                drawPanels();
                if (++frameNumber % 15 == 0) {
                    int count = detectedPlanes.size();
                    String message;
                    if (count == 0) {
                        message = input.trackingStatus() == MotionTrackingStatus.Tracking
                            ? "Tracking. No planes yet; sweep sideways across the wall and include its edges."
                            : "Move slowly across textured surfaces to improve tracking.";
                    } else {
                        String quality = input.trackingStatus() == MotionTrackingStatus.Tracking ? "Tracking." : "Tracking is weak.";
                        message = quality + " " + count + " surfaces detected. Tap or drag READY to place.";
                    }
                    reportIfChanged(message);
                }
            } finally {
                cameraParameters.dispose(); image.dispose(); input.dispose(); output.dispose();
            }
        }

        void setReadySize(float width, float height) { panelWidth = width; panelHeight = height; }
        void setBitmap(Bitmap bitmap) { bitmapToUpload.set(bitmap); }
        void resizeSelected(float factor) {
            if (selectedPanel >= 0 && selectedPanel < panels.size()) {
                PanelPlacement panel = panels.get(selectedPanel);
                panel.scale = Math.max(0.25f, Math.min(4f, panel.scale * factor));
                reportIfChanged("Selected panel resized to " + Math.round(panel.scale * 100) + "%.");
            } else {
                reportIfChanged("Tap a placed panel to select it, then pinch with two fingers to resize.");
            }
        }
        void deleteSelected() {
            if (selectedPanel >= 0 && selectedPanel < panels.size()) {
                panels.remove(selectedPanel); selectedPanel = -1;
                reportIfChanged("Selected panel removed.");
            }
        }
        void clearPanels() { panels.clear(); selectedPanel = -1; reportIfChanged("Placed panels cleared."); }

        void beginPointer(float x, float y, int width, int height) {
            selectedPanel = findPanelAt(x, y);
            pointerStartedOnPanel = selectedPanel >= 0;
            if (pointerStartedOnPanel) {
                reportIfChanged("Panel selected. Pinch with two fingers to resize, or drag to move it.");
            }
        }
        void cancelPointer() { pointerStartedOnPanel = false; }
        void movePointer(float x, float y, int width, int height) {
            if (!pointerStartedOnPanel || selectedPanel < 0 || spatialMap == null) return;
            Vec2F imagePoint = screenToImage(x, y, width, height);
            if (imagePoint == null) return;
            List<Vec3F> hits = spatialMap.hitTestAgainstPlanes(imagePoint);
            if (!hits.isEmpty()) {
                PanelPlacement panel = panels.get(selectedPanel);
                float[] world = hits.get(0).data;
                panel.model[12] = world[0] + panel.normal[0] * 0.004f;
                panel.model[13] = world[1] + panel.normal[1] * 0.004f;
                panel.model[14] = world[2] + panel.normal[2] * 0.004f;
            }
        }
        void endPointer(float x, float y, int width, int height) {
            if (!pointerStartedOnPanel && selectedPanel < 0) placePanel(x, y, width, height);
            pointerStartedOnPanel = false;
        }

        void placePanel(float x, float y, int width, int height) {
            if (spatialMap == null || camera == null) return;
            Vec2F imagePoint = screenToImage(x, y, width, height);
            if (imagePoint == null) { reportIfChanged("Scanning; point the camera at a wall or ceiling and move slowly."); return; }
            List<Vec3F> hits = spatialMap.hitTestAgainstPlanes(imagePoint);
            if (hits.isEmpty()) { reportIfChanged("No scanned surface here yet. Sweep the camera slowly over the wall or ceiling."); return; }
            float[] hit = hits.get(0).data.clone();
            List<PlaneData> planes = spatialMap.getMapPlanes();
            PlaneData nearest = null;
            float nearestDistance = Float.MAX_VALUE;
            for (PlaneData plane : planes) {
                float[] pose = plane.getPose().data;
                float[] glPose = toGl(pose);
                float dx = hit[0] - glPose[12], dy = hit[1] - glPose[13], dz = hit[2] - glPose[14];
                float distance = dx * dx + dy * dy + dz * dz;
                if (distance < nearestDistance) { nearestDistance = distance; nearest = plane; }
            }
            if (nearest == null) { planes.forEach(PlaneData::dispose); reportIfChanged("Surface detected; hold the camera steady and try again."); return; }
            float[] pose = toGl(nearest.getPose().data);
            int planeType = nearest.getType();
            planes.forEach(PlaneData::dispose);
            pose[12] = hit[0] + pose[4] * 0.004f;
            pose[13] = hit[1] + pose[5] * 0.004f;
            pose[14] = hit[2] + pose[6] * 0.004f;
            float[] normal = new float[]{pose[4], pose[5], pose[6]};
            float placedWidth = panelWidth;
            float placedHeight = panelHeight;
            if (planeType == PlaneType.Vertical && Math.abs(pose[1]) > Math.abs(pose[9])) {
                // The plane's local X axis is closer to world-up. Rotate the quad in-plane
                // so the product's long dimension remains vertical on a wall.
                float x0 = pose[0], x1 = pose[1], x2 = pose[2];
                pose[0] = pose[8]; pose[1] = pose[9]; pose[2] = pose[10];
                pose[8] = -x0; pose[9] = -x1; pose[10] = -x2;
                placedWidth = panelHeight;
                placedHeight = panelWidth;
            }
            panels.add(new PanelPlacement(pose, normal, placedWidth, placedHeight, panelTexture, planeType));
            selectedPanel = panels.size() - 1;
            reportIfChanged((planeType == PlaneType.Vertical ? "Wall" : "Horizontal surface") + " panel placed at tracked scale. Tap another surface to add one.");
        }

        private Vec2F screenToImage(float x, float y, int width, int height) {
            OutputFrame current = outputFrames == null ? null : outputFrames.peek();
            if (current == null) return null;
            InputFrame frame = current.inputFrame();
            if (frame == null) { current.dispose(); return null; }
            CameraParameters parameters = frame.cameraParameters();
            Vec2F result = parameters.imageCoordinatesFromScreenCoordinates(
                (float) Math.max(1, width) / Math.max(1, height), screenRotation(), true, false,
                new Vec2F(x / Math.max(1, width), y / Math.max(1, height))
            );
            parameters.dispose(); frame.dispose(); current.dispose();
            return result;
        }

        private int findPanelAt(float x, float y) {
            if (projectionGl == null || viewGl == null) return -1;
            for (int i = panels.size() - 1; i >= 0; i--) {
                PanelPlacement panel = panels.get(i);
                float halfWidth = panel.width * panel.scale * 0.5f;
                float halfHeight = panel.height * panel.scale * 0.5f;
                float[] corners = {
                    -halfWidth, 0f, -halfHeight, 1f,
                     halfWidth, 0f, -halfHeight, 1f,
                     halfWidth, 0f,  halfHeight, 1f,
                    -halfWidth, 0f,  halfHeight, 1f
                };
                float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
                float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
                boolean visible = true;
                for (int corner = 0; corner < 4; corner++) {
                    float[] world = new float[4];
                    Matrix.multiplyMV(world, 0, panel.model, 0, corners, corner * 4);
                    float[] screen = project(world[0], world[1], world[2]);
                    if (screen == null) { visible = false; break; }
                    minX = Math.min(minX, screen[0]); maxX = Math.max(maxX, screen[0]);
                    minY = Math.min(minY, screen[1]); maxY = Math.max(maxY, screen[1]);
                }
                if (visible && x >= minX && x <= maxX && y >= minY && y <= maxY) return i;
            }
            return -1;
        }

        private float[] project(float x, float y, float z) {
            float[] world = {x, y, z, 1f};
            float[] cameraPoint = new float[4];
            float[] clip = new float[4];
            Matrix.multiplyMV(cameraPoint, 0, viewGl, 0, world, 0);
            Matrix.multiplyMV(clip, 0, projectionGl, 0, cameraPoint, 0);
            if (clip[3] <= 0f) return null;
            float sx = (clip[0] / clip[3] + 1f) * viewportWidth * 0.5f;
            float sy = (1f - clip[1] / clip[3]) * viewportHeight * 0.5f;
            return new float[]{sx, sy};
        }

        private void updateBitmapTexture() {
            Bitmap bitmap = bitmapToUpload.getAndSet(null);
            if (bitmap == null || bitmap.isRecycled()) return;
            int[] textures = new int[1]; GLES30.glGenTextures(1, textures, 0); panelTexture = textures[0];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, panelTexture);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            for (PanelPlacement panel : panels) if (panel.texture == 0) panel.texture = panelTexture;
        }

        private void drawDetectedPlanes(List<PlaneData> planes) {
            if (planes == null) return;
            GLES30.glUseProgram(panelProgram);
            GLES30.glUniform1i(GLES30.glGetUniformLocation(panelProgram, "useTexture"), 0);
            GLES30.glUniform4f(GLES30.glGetUniformLocation(panelProgram, "solidColor"), 0.2f, 1f, 0.55f, 0.9f);
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            for (PlaneData plane : planes) {
                float[] model = toGl(plane.getPose().data);
                drawQuad(model, Math.min(plane.getExtentX(), 3f), Math.min(plane.getExtentZ(), 3f), 0, GLES30.GL_LINE_LOOP);
            }
            GLES30.glDisable(GLES30.GL_BLEND);
            planes.forEach(PlaneData::dispose);
        }

        private void drawPanels() {
            GLES30.glUseProgram(panelProgram);
            for (int i = 0; i < panels.size(); i++) {
                PanelPlacement panel = panels.get(i);
                GLES30.glUniform1i(GLES30.glGetUniformLocation(panelProgram, "useTexture"), panel.texture > 0 ? 1 : 0);
                GLES30.glUniform4f(GLES30.glGetUniformLocation(panelProgram, "solidColor"), 0.72f, 0.34f, 0.12f, 1f);
                drawQuad(panel.model, panel.width * panel.scale, panel.height * panel.scale, panel.texture, GLES30.GL_TRIANGLE_STRIP);
                if (i == selectedPanel) {
                    GLES30.glUniform1i(GLES30.glGetUniformLocation(panelProgram, "useTexture"), 0);
                    GLES30.glUniform4f(GLES30.glGetUniformLocation(panelProgram, "solidColor"), 1f, 0.72f, 0.2f, 1f);
                    drawQuad(panel.model, panel.width * panel.scale * 1.04f, panel.height * panel.scale * 1.04f, 0, GLES30.GL_LINE_LOOP);
                }
            }
        }

        private void drawQuad(float[] model, float width, float height, int texture, int primitive) {
            float[] vertices = primitive == GLES30.GL_LINE_LOOP
                ? new float[]{
                    -width/2, 0, -height/2, 0, 0,  width/2, 0, -height/2, 1, 0,
                     width/2, 0,  height/2, 1, 1, -width/2, 0,  height/2, 0, 1
                }
                : new float[]{
                    -width/2, 0, -height/2, 0, 0,  width/2, 0, -height/2, 1, 0,
                    -width/2, 0,  height/2, 0, 1,  width/2, 0,  height/2, 1, 1
                };
            FloatBuffer data = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            data.put(vertices).position(0);
            int position = GLES30.glGetAttribLocation(panelProgram, "position");
            int uv = GLES30.glGetAttribLocation(panelProgram, "uv");
            GLES30.glEnableVertexAttribArray(position);
            GLES30.glVertexAttribPointer(position, 3, GLES30.GL_FLOAT, false, 20, data);
            data.position(3);
            GLES30.glEnableVertexAttribArray(uv);
            GLES30.glVertexAttribPointer(uv, 2, GLES30.GL_FLOAT, false, 20, data);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(panelProgram, "projection"), 1, false, projectionGl, 0);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(panelProgram, "view"), 1, false, viewGl, 0);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(panelProgram, "model"), 1, false, model, 0);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
            GLES30.glUniform1i(GLES30.glGetUniformLocation(panelProgram, "panelTexture"), 0);
            GLES30.glDrawArrays(primitive, 0, 4);
            GLES30.glDisableVertexAttribArray(position);
            GLES30.glDisableVertexAttribArray(uv);
        }

        private void createPanelProgram() {
            String vertex = "#version 300 es\n in vec3 position; in vec2 uv; uniform mat4 projection; uniform mat4 view; uniform mat4 model; out vec2 tex; void main(){ tex=uv; gl_Position=projection*view*model*vec4(position,1.0); }";
            String fragment = "#version 300 es\n precision mediump float; in vec2 tex; uniform sampler2D panelTexture; uniform bool useTexture; uniform vec4 solidColor; out vec4 color; void main(){ color=useTexture?texture(panelTexture,tex):solidColor; }";
            int vs = compileShader(GLES30.GL_VERTEX_SHADER, vertex);
            int fs = compileShader(GLES30.GL_FRAGMENT_SHADER, fragment);
            panelProgram = GLES30.glCreateProgram();
            GLES30.glAttachShader(panelProgram, vs); GLES30.glAttachShader(panelProgram, fs); GLES30.glLinkProgram(panelProgram);
            GLES30.glDeleteShader(vs); GLES30.glDeleteShader(fs);
        }

        private int compileShader(int kind, String source) {
            int shader = GLES30.glCreateShader(kind); GLES30.glShaderSource(shader, source); GLES30.glCompileShader(shader);
            int[] status = new int[1]; GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) { String error = GLES30.glGetShaderInfoLog(shader); GLES30.glDeleteShader(shader); throw new IllegalStateException(error); }
            return shader;
        }

        private void reportIfChanged(String message) {
            if (!message.equals(lastStatus)) { lastStatus = message; reportStatus(message); }
        }

        private int screenRotation() {
            int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_90) return 90;
            if (rotation == Surface.ROTATION_180) return 180;
            if (rotation == Surface.ROTATION_270) return 270;
            return 0;
        }

        private float[] toGl(float[] rowMajor) {
            return new float[]{rowMajor[0],rowMajor[4],rowMajor[8],rowMajor[12], rowMajor[1],rowMajor[5],rowMajor[9],rowMajor[13], rowMajor[2],rowMajor[6],rowMajor[10],rowMajor[14], rowMajor[3],rowMajor[7],rowMajor[11],rowMajor[15]};
        }
    }

    private static final class PanelPlacement {
        final float[] model;
        final float[] normal;
        final float width;
        final float height;
        int texture;
        final int planeType;
        float scale = 1f;
        PanelPlacement(float[] model, float[] normal, float width, float height, int texture, int planeType) {
            this.model = model; this.normal = normal; this.width = width; this.height = height; this.texture = texture; this.planeType = planeType;
        }
    }
}
