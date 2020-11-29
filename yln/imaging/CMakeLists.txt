add_library(imaging image.c rgba.c kernel.c imageio.c)

target_link_libraries(imaging PUBLIC
        util
        )

target_include_directories(imaging PUBLIC
        ${PROJECT_SOURCE_DIR}/util
        ${LIBPNG_INCLUDE}
        )
