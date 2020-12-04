//
// Created by Philip Boger on 03.12.20.
//

#include <image.h>
#include <lauxlib.h>
#include <lualib.h>
#include <luaconf.h>
#include "luakernel.h"

static int new_kernel(lua_State *L) {
    int width = luaL_checkinteger(L, 1);
    int height = luaL_checkinteger(L, 2);
    float value = luaL_checknumber(L, 3);
    luaL_argcheck(L, width > 0, 1, "width must be positive");
    luaL_argcheck(L, height > 0, 2, "height must be positive");
    Kernel *kernel = lua_newuserdata(L, sizeof(Kernel));
    luaL_setmetatable(L, YLN_KERNEL);
    init_kernel(kernel, width, height, value);
    return 1;
}

static int new_kernel_of_values(lua_State *L) {
    int width = luaL_checkinteger(L, 1);
    int height = luaL_checkinteger(L, 2);

    lua_len(L, 3); // push table len
    int table_len = lua_tointeger(L, -1);
    lua_pop(L, 1); // pop table len

    luaL_argcheck(L, width > 0, 1, "width must be positive");
    luaL_argcheck(L, width * height == table_len, 3, "table must have width * height elements");

    Kernel *kernel = (Kernel *)lua_newuserdata(L, sizeof(Kernel));
    luaL_setmetatable(L, YLN_KERNEL);
    init_kernel(kernel, width, height, 0);

    lua_pushnil(L);  // push initial (dummy) key
    for (float *value_ptr = kernel->values; lua_next(L, 3) != 0; value_ptr++) {
        *value_ptr = lua_tonumber(L, -1); // 'key' is at index -2 and 'value' is at index -1
        lua_pop(L, 1); // removes 'value'; keeps 'key' for next iteration */
    }
    return 1;
}

static int get_kernel_width(lua_State *L) {
    lua_pushinteger(L, to_kernel(L, 1)->width);
    return 1;
}

static int get_kernel_height(lua_State *L) {
    lua_pushinteger(L, to_kernel(L, 1)->height);
    return 1;
}

static float *get_kernel_value_addr(lua_State *L) {
    Kernel *kernel = to_kernel(L, 1);
    lua_Integer x = luaL_checkinteger(L, 2) - 1; // ranges from 1..width] as usual in lua
    lua_Integer y = luaL_checkinteger(L, 3) - 1;
    luaL_argcheck(L, kernel->values != NULL, 1, "kernel is uninitialized");
    luaL_argcheck(L, 0 <= x && x < kernel->width, 2, "x is out of range");
    luaL_argcheck(L, 0 <= y && y < kernel->height, 3, "y is out of range");
    return &kernel->values[y * kernel->width + x];
}

static int get_kernel_value(lua_State *L) {
    lua_pushnumber(L, *get_kernel_value_addr(L));
    return 1;
}

static int set_kernel_value(lua_State *L) {
    float *ptr = get_kernel_value_addr(L);
    *ptr = luaL_checknumber(L, 4);
    return 0;
}

static const struct luaL_Reg function_lib[] = {
        {"new",    new_kernel},
        {"of",     new_kernel_of_values},
        {NULL, NULL},
};

static const struct luaL_Reg method_lib[] = {
        {"width",  get_kernel_width},
        {"height", get_kernel_height},
        {"get",    get_kernel_value},
        {"set",    set_kernel_value},
        {NULL, NULL},
};

static int do_gc (lua_State *L) {
    Kernel *p = to_kernel(L, 1);
    free_kernel(p);
    return 0;
}

static const luaL_Reg metameth_lib[] = {
        {"__gc", do_gc},
        {NULL, NULL}
};

Kernel *to_kernel(lua_State *L, int arg) {
    void *userdata = luaL_checkudata(L, arg, YLN_KERNEL);
    luaL_argcheck(L, userdata != NULL, arg, "`kernel` expected");
    return (Kernel *)userdata;
}

int luaopen_kernel(lua_State *L) {
    luaL_newlib(L, function_lib);
    luaL_newmetatable(L, YLN_KERNEL);  // metatable for file handles
    luaL_setfuncs(L, metameth_lib, 0);  // add metamethods to new metatable
    luaL_newlib(L, method_lib);
    lua_setfield(L, -2, "__index");  /* metatable.__index = method table */
    lua_pop(L, 1);  // pop metatable
    return 1;
}
