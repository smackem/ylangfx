//
// Created by philip on 10.12.20.
//

#include <lauxlib.h>
#include <lualib.h>
#include <luaconf.h>
#include "luacolor.h"

static int new_color(lua_State *L) {
    float r = luaL_checknumber(L, 1);
    float g = luaL_checknumber(L, 2);
    float b = luaL_checknumber(L, 3);
    float a = luaL_checknumber(L, 4);
    Color *color = (Color *)lua_newuserdata(L, sizeof(Color));
    luaL_setmetatable(L, YLN_COLOR);
    set_color(color, r, g, b, a);
    return 1;
}

