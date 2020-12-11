//
// Created by Philip Boger on 28.11.20.
//

#include <lualib.h>
#include <lauxlib.h>
#include <types.h>
#include "luargba.h"
#include "luaimage.h"
#include "luakernel.h"
#include "luacolor.h"
#include "luabind.h"

static void register_libs(lua_State *L) {
    luaL_requiref(L, "rgba", luaopen_rgba, true);
    luaL_requiref(L, "image", luaopen_image, true);
    luaL_requiref(L, "kernel", luaopen_kernel, true);
    luaL_requiref(L, "color", luaopen_color, true);
    lua_pop(L, 4);
}

error run_lua_script(const char *script_path, ImageFloat *dest, const ImageFloat *orig) {
    lua_State *L = luaL_newstate();   // opens Lua
    int error = OK;
    luaL_openlibs(L);
    register_libs(L);
    // store input image in global variable 'in'
    push_image(L, orig);
    lua_setglobal(L, "inp");

    do {
        error = luaL_dofile(L, script_path);
        if (error) {
            trace("%s\n", lua_tostring(L, -1));
            break;
        }
        const ImageFloat *result = to_image(L, -1);
        if (result == NULL) {
            error = 1;
            break;
        }
        clone_image(dest, result);
    } ONCE;

    lua_close(L);
    return error;
}
