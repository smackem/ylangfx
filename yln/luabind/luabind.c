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
    lua_pop(L, 3);
}

error run_lua_script(const char *script_path, ImageRgba *dest, const ImageRgba *orig) {
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
        const ImageRgba *result = luaL_testudata(L, -1, YLN_IMAGE);
        if (result == NULL) {
            error = 1;
            break;
        }
        clone_image(dest, result);
    } ONCE;

    lua_close(L);
    return error;
}
