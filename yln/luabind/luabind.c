//
// Created by Philip Boger on 28.11.20.
//

#include <lualib.h>
#include <lauxlib.h>
#include <types.h>
#include "luabind.h"

static void register_libs(lua_State *L) {
    luaL_requiref(L, "image", luaopen_image, true);
    lua_pop(L, 1);
}

void test_lua() {
    int error;
    lua_State *L = luaL_newstate();   // opens Lua
    luaL_openlibs(L);
    register_libs(L);
    const char *source = "i = image.new(100, 200)\n"
                         "return image.at(i, 10, 11)\n";

    do {
        error = luaL_loadbuffer(L, source, strlen(source), "line") ||
                lua_pcall(L, 0, 1, 0);

        if (error) {
            TRACE("%s\n", lua_tostring(L, -1));
            lua_pop(L, 1);  // pop error message from the stack
            break;
        }
//        int type = lua_getglobal(L, "result");
//        if (type != LUA_TNUMBER) {
//            TRACE("type: %d\n", type);
//            break;
//        }
        lua_Integer result = luaL_checkinteger(L, -1);
        wprintf(L"lua result: %d\n", result);
    } ONCE;

    lua_close(L);
}
