attribute vec4 vPosition;//形状，顶点数据。有四个顶点
attribute vec4 vCoord;//四个顶点
uniform mat4 vMatrix;//摄像头矩阵

varying vec2 aCoord;//给片段着色器的像素点
void main(){
    gl_Position = vPosition;
    aCoord = (vMatrix*vCoord).xy;//摄像头只需要x,y；一共有x,y,z,向量
}