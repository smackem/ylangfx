add_library(imaging imaging.c)

target_link_libraries(imaging PUBLIC
        util
        )

target_include_directories(imaging PUBLIC
        ${PROJECT_SOURCE_DIR}/util
        )
