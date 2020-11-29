//
// Created by Philip Boger on 28.11.20.
//

#include <lualib.h>
#include <lauxlib.h>
#include <imaging.h>
#include "luabind.h"

void test_lua() {
    int error;
    lua_State *L = luaL_newstate();   // opens Lua
    luaL_openlibs(L);
    const char *source = "print(\"hello from lua!\")\n"
                         "return 100\n";

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
