# prerequisites
cmake_minimum_required(VERSION 3.17)

# project definition with config header
project(yln LANGUAGES C VERSION 0.1)
configure_file(build_config.h.in build_config.h)

# language level
set(CMAKE_C_STANDARD 99)

# add imaging library
add_subdirectory(imaging)

# main target with libraries
add_executable(yln main.c imageio.c)
target_link_libraries(yln PUBLIC imaging)

# include directories
target_include_directories(yln PUBLIC
        "${PROJECT_BINARY_DIR}"
        "${PROJECT_SOURCE_DIR}/imaging"
        )
