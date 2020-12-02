//
// Created by philip on 02.12.20.
//

#include <image.h>
#include <lauxlib.h>
#include <lualib.h>
#include <luaconf.h>
#include "luaimage.h"

static int new_image(lua_State *L) {
    int width = luaL_checkinteger(L, 1);
    int height = luaL_checkinteger(L, 2);
    ImageRgba *image = lua_newuserdata(L, sizeof(ImageRgba));
    image->width = width;
    image->height = height;
    return 1;
}

static int get_image_width(lua_State *L) {
    ImageRgba *image = lua_touserdata(L, 1);
    luaL_argcheck(L, image != NULL, 1, "`image` expected");
    lua_pushinteger(L, image->width);
    return 1;
}

static int get_image_height(lua_State *L) {
    ImageRgba *image = lua_touserdata(L, 1);
    luaL_argcheck(L, image != NULL, 1, "`image` expected");
    lua_pushinteger(L, image->height);
    return 1;
}

static int get_image_pixel(lua_State *L) {
    ImageRgba *image = lua_touserdata(L, 1);
    luaL_argcheck(L, image != NULL, 1, "`image` expected");
    int x = luaL_checkinteger(L, 2);
    int y = luaL_checkinteger(L, 3);
    lua_pushinteger(L, x + y);
    return 1;
}

static const struct luaL_Reg image_lib[] = {
        {"new",    new_image},
        {"width",  get_image_width},
        {"height", get_image_height},
        {"at",     get_image_pixel},
        {NULL, NULL},
};

int luaopen_image(lua_State *L) {
    luaL_newlib(L, image_lib);
    return 1;
}
