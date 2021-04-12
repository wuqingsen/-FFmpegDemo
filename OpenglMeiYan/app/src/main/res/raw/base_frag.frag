precision mediump float;
//采样点的坐标
varying vec2 aCoord;

//采样器，使用正常的sampler2D
uniform sampler2D vTexture;

void main(){
    gl_FragColor = texture2D(vTexture, aCoord);
}