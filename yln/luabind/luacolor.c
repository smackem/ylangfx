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

static int get_color_red(lua_State *L) {
    Color *color = (Color *)to_color(L, 1);
    lua_pushnumber(L, color->red);
    return 1;
}

static int get_color_green(lua_State *L) {
    Color *color = (Color *)to_color(L, 1);
    lua_pushnumber(L, color->green);
    return 1;
}

static int get_color_blue(lua_State *L) {
    Color *color = (Color *)to_color(L, 1);
    lua_pushnumber(L, color->blue);
    return 1;
}

static int get_color_alpha(lua_State *L) {
    Color *color = (Color *)to_color(L, 1);
    lua_pushnumber(L, color->alpha);
    return 1;
}

static const struct luaL_Reg function_lib[] = {
        {"new",    new_color},
        {NULL, NULL},
};

static const struct luaL_Reg method_lib[] = {
        {"red",  get_color_red},
        {"green", get_color_green},
        {"blue",    get_color_blue},
        {"alpha",    get_color_alpha},
        {NULL, NULL},
};

Color *to_color(lua_State *L, int arg) {
    void *userdata = luaL_checkudata(L, arg, YLN_COLOR);
    luaL_argcheck(L, userdata != NULL, arg, "`color` expected");
    return (Color *)userdata;
}

