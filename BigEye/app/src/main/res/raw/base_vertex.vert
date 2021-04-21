attribute vec4 vPosition;//形状，顶点数据。有四个顶点
attribute vec2 vCoord;//二维

varying vec2 aCoord;//给片段着色器的像素点
void main(){
    gl_Position = vPosition;
    aCoord = vCoord;
}