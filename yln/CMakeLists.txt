# prerequisites
cmake_minimum_required(VERSION 3.17)

# project definition with config header
project(yln LANGUAGES C VERSION 0.1)
configure_file(build_config.h.in build_config.h)

# language level
set(CMAKE_C_STANDARD 11)
#set(USR_LOCAL_DIR /usr/local)

# add imaging library
add_subdirectory(imaging)

# main target with libraries
add_executable(yln main.c imageio.c)

# reference libraries
find_library(LIBPNG_LIB NAMES libpng.a)
message(STATUS ${LIBPNG_LIB})

find_library(ZLIB_LIB NAMES libz.a)
message(STATUS ${ZLIB_LIB})

target_link_libraries(yln PUBLIC
        imaging
        ${LIBPNG_LIB}
        ${ZLIB_LIB}
        )

# add include directories
find_path(LIBPNG_INCLUDE NAMES png.h)
message(STATUS ${LIBPNG_INCLUDE})

target_include_directories(yln PUBLIC
        "${PROJECT_BINARY_DIR}"
        "${PROJECT_SOURCE_DIR}/imaging"
        "${LIBPNG_INCLUDE}"
        )
