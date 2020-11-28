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

# main target with libraries
add_executable(yln main.c imageio.c)

# reference libraries
find_library(LIBPNG_LIB NAMES png)
message(STATUS ${LIBPNG_LIB}) # on linux and macos: /usr/local/lib/libpng.a

find_library(ZLIB_LIB NAMES z)
message(STATUS ${ZLIB_LIB})

find_library(LUA_LIB NAMES lua)
message(STATUS ${LUA_LIB})

target_link_libraries(yln PUBLIC
        imaging
        util
        luabind
        ${LIBPNG_LIB}
        ${ZLIB_LIB}
        ${LUA_LIB}
        )

# add include directories
find_path(LIBPNG_INCLUDE NAMES png.h)
message(STATUS ${LIBPNG_INCLUDE})

find_path(LIBLUA_INCLUDE NAMES lua.h)
message(STATUS ${LIBLUA_INCLUDE})

target_include_directories(yln PUBLIC
        "${PROJECT_BINARY_DIR}"
        "${PROJECT_SOURCE_DIR}/imaging"
        "${PROJECT_SOURCE_DIR}/util"
        "${PROJECT_SOURCE_DIR}/luabind"
        "${LIBPNG_INCLUDE}"
        "${LIBLUA_INCLUDE}"
        )
