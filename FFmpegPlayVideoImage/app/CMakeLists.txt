cmake_minimum_required(VERSION 3.10.2)

project("ffmpegwudemo")

#指定编译文件
file(GLOB SOURCE src/main/cpp/*.cpp)

add_library(
        wangyiplayer
        SHARED
        ${SOURCE})

find_library(
        log-lib
        log )

#引入头文件夹
include_directories(src/main/cpp/include)

#设置变量
set(my_lib_path ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${my_lib_path}")

target_link_libraries(
        wangyiplayer
        avfilter avformat avcodec avutil swresample swscale
        ${log-lib}
        android
        z
        OpenSLES)