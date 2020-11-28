add_library(luabind luabind.c)

target_link_libraries(luabind PUBLIC
        util
        imaging
        )

find_path(LIBLUA_INCLUDE NAMES lua.h)
message(STATUS ${LIBLUA_INCLUDE})

target_include_directories(luabind PUBLIC
        ${PROJECT_SOURCE_DIR}/util
        ${PROJECT_SOURCE_DIR}/imaging
        "${LIBLUA_INCLUDE}"
        )
