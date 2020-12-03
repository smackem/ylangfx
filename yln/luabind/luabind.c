//
// Created by Philip Boger on 28.11.20.
//

#include <lualib.h>
#include <lauxlib.h>
#include <types.h>
#include "luargba.h"
#include "luaimage.h"
#include "luakernel.h"
#include "luabind.h"

static void register_libs(lua_State *L) {
    luaL_requiref(L, "rgba", luaopen_rgba, true);
    luaL_requiref(L, "image", luaopen_image, true);
    luaL_requiref(L, "kernel", luaopen_kernel, true);
    lua_pop(L, 2);
}

void run_lua_script(const char *script_path) {
    lua_State *L = luaL_newstate();   // opens Lua
    luaL_openlibs(L);
    register_libs(L);
    int error = luaL_dofile(L, script_path);

    if (error) {
        TRACE("%s\n", lua_tostring(L, -1));
    } else {
        lua_Integer result = luaL_checkinteger(L, -1);
        wprintf(L"lua result: %d\n", result);
    }

    lua_close(L);
}
