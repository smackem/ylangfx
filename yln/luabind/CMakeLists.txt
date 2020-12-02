add_library(luabind luabind.c luaimage.c luaimage.h)

set(EXTRA_LIBS m dl) # standard libs that explicity need to be linked ('gcc -lm -ldl ...')
message(STATUS ${EXTRA_LIBS})

target_link_libraries(luabind PUBLIC
        ${EXTRA_LIBS}
        util
        imaging
        )

find_path(LIBLUA_INCLUDE NAMES lua.h)
message(STATUS ${LIBLUA_INCLUDE})

target_include_directories(luabind PUBLIC
        "${PROJECT_SOURCE_DIR}/util"
        "${PROJECT_SOURCE_DIR}/imaging"
        "${LIBLUA_INCLUDE}"
        )
