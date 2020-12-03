//
// Created by Philip Boger on 03.12.20.
//

#include <lualib.h>
#include <lauxlib.h>
#include <imaging.h>
#include "luargba.h"

static int new_rgb(lua_State *L) {
    lua_Integer r = luaL_checkinteger(L, 1);
    lua_Integer g = luaL_checkinteger(L, 2);
    lua_Integer b = luaL_checkinteger(L, 3);
    lua_pushinteger(L, RGBA(r, g, b, 255));
    return 1;
}

static int new_rgba(lua_State *L) {
    lua_Integer r = luaL_checkinteger(L, 1);
    lua_Integer g = luaL_checkinteger(L, 2);
    lua_Integer b = luaL_checkinteger(L, 3);
    lua_Integer a = luaL_checkinteger(L, 4);
    lua_pushinteger(L, RGBA(r, g, b, a));
    return 1;
}

static int get_r(lua_State *L) {
    rgba color = (rgba)luaL_checkinteger(L, 1);
    lua_pushinteger(L, R(color));
    return 1;
}

static int get_g(lua_State *L) {
    rgba color = (rgba)luaL_checkinteger(L, 1);
    lua_pushinteger(L, G(color));
    return 1;
}

static int get_b(lua_State *L) {
    rgba color = (rgba)luaL_checkinteger(L, 1);
    lua_pushinteger(L, B(color));
    return 1;
}

static int get_a(lua_State *L) {
    rgba color = (rgba)luaL_checkinteger(L, 1);
    lua_pushinteger(L, A(color));
    return 1;
}

static const struct luaL_Reg function_lib[] = {
        {"rgb",  new_rgb},
        {"rgba", new_rgba},
        {"r",    get_r},
        {"g",    get_g},
        {"b",    get_b},
        {"a",    get_a},
        {NULL, NULL},
};

int luaopen_rgba(lua_State *L) {
    luaL_newlib(L, function_lib);
    return 1;
}
