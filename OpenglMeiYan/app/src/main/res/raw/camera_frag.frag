#extension GL_OES_EGL_image_external : require
precision mediump float;
//采样点的坐标
varying vec2 aCoord;

//采样器，使用正常的sampler2D
uniform samplerExternalOES vTexture;

void main(){
    gl_FragColor = texture2D(vTexture, aCoord);
}