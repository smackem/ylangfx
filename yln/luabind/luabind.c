//
// Created by Philip Boger on 28.11.20.
//

#include <imaging.h>
#include "luabind.h"
#include <lualib.h>
#include <lauxlib.h>

void test_lua() {
    int error;
    lua_State *L = luaL_newstate();   // opens Lua
    luaL_openlibs(L);
    const char *source = "print(\"hello to \", 1 + 1)";

    error = luaL_loadbuffer(L, source, strlen(source), "line") ||
            lua_pcall(L, 0, 0, 0);
    if (error) {
        TRACE("%s", lua_tostring(L, -1));
        lua_pop(L, 1);  // pop error message from the stack
    }
    lua_close(L);
}
