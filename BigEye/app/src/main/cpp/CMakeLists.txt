cmake_minimum_required(VERSION 3.10.2)

project("bigeye")

add_library( native-lib

        SHARED

        native-lib.cpp
        FaceTrack.cpp)
#             ../../../../src/main/cpp/native-lib.cpp )
#导入头文件
include_directories(include)
#include_directories(../../../../src/main/cpp/include)

#导入库文件
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}../../../../libs/${ANDROID_ABI}")
find_library( log-lib
        log )

target_link_libraries(
        native-lib

        opencv_java4

        android

        ${log-lib} )