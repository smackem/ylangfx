# prerequisites
cmake_minimum_required(VERSION 3.17)

# project definition with config header
project(yln LANGUAGES C VERSION 0.1)
configure_file(build_config.h.in build_config.h)

# language level
set(CMAKE_C_STANDARD 11)

# add child projects
add_subdirectory(imaging)
add_subdirectory(util)
add_subdirectory(luabind)

# reference libraries common to all targets
find_library(LIBPNG_LIB NAMES png)
message(STATUS ${LIBPNG_LIB})

find_library(ZLIB_LIB NAMES z)
message(STATUS ${ZLIB_LIB})

find_library(LUA_LIB NAMES lua)
message(STATUS ${LUA_LIB})

link_libraries(
        imaging
        util
        luabind
        ${LIBPNG_LIB}
        ${ZLIB_LIB}
        ${LUA_LIB}
        )

# add include directories for all targets
find_path(LIBPNG_INCLUDE NAMES png.h)
message(STATUS ${LIBPNG_INCLUDE})

find_path(LIBLUA_INCLUDE NAMES lua.h)
message(STATUS ${LIBLUA_INCLUDE})

include_directories(
        "${PROJECT_BINARY_DIR}"
        "${PROJECT_SOURCE_DIR}/imaging"
        "${PROJECT_SOURCE_DIR}/util"
        "${LIBPNG_INCLUDE}"
        )

# main target
add_executable(ylnx main.c)

# lib target
add_library(yln SHARED lib.c)

target_include_directories(yln PUBLIC
        "$ENV{JAVA_HOME}/include"
        "$ENV{JAVA_HOME}/include/${CMAKE_SYSTEM_NAME}"
        )

# test target
add_executable(yln_test test.c)

# libraries specific to yln_test
target_link_libraries(yln_test PUBLIC
        luabind
        ${LUA_LIB}
        )

target_include_directories(yln_test PUBLIC
        "${PROJECT_SOURCE_DIR}/luabind"
        "${LIBLUA_INCLUDE}"
        )
