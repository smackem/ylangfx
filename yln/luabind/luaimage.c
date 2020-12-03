//
// Created by philip on 02.12.20.
//

#include <image.h>
#include <lauxlib.h>
#include <lualib.h>
#include <luaconf.h>
#include "luaimage.h"

#define YLN_IMAGE "Yln.image"

static int new_image(lua_State *L) {
    int width = luaL_checkinteger(L, 1);
    int height = luaL_checkinteger(L, 2);
    luaL_argcheck(L, width > 0, 1, "width must be positive");
    luaL_argcheck(L, height > 0, 2, "height must be positive");
    ImageRgba *image = lua_newuserdata(L, sizeof(ImageRgba));
    luaL_setmetatable(L, YLN_IMAGE);
    init_image(image, width, height);
    return 1;
}

static ImageRgba *to_image (lua_State *L) {
    void *userdata = luaL_checkudata(L, 1, YLN_IMAGE);
    luaL_argcheck(L, userdata != NULL, 1, "`image` expected");
    return (ImageRgba *)userdata;
}

static int get_image_width(lua_State *L) {
    lua_pushinteger(L, to_image(L)->width);
    return 1;
}

static int get_image_height(lua_State *L) {
    lua_pushinteger(L, to_image(L)->height);
    return 1;
}

static int get_image_pixel(lua_State *L) {
    ImageRgba *image = to_image(L);
    int x = luaL_checkinteger(L, 2);
    int y = luaL_checkinteger(L, 3);
    luaL_argcheck(L, image->pixels != NULL, 1, "image is uninitialized");
    luaL_argcheck(L, x < image->width, 2, "x is out of range");
    luaL_argcheck(L, y < image->height, 3, "y is out of range");
    lua_pushinteger(L, image->pixels[y * image->width + x]);
    return 1;
}

static int set_image_pixel(lua_State *L) {
    ImageRgba *image = to_image(L);
    int x = luaL_checkinteger(L, 2);
    int y = luaL_checkinteger(L, 3);
    int color = luaL_checkinteger(L, 4);
    luaL_argcheck(L, image->pixels != NULL, 1, "image is uninitialized");
    luaL_argcheck(L, x < image->width, 2, "x is out of range");
    luaL_argcheck(L, y < image->height, 3, "y is out of range");
    image->pixels[y * image->width + x] = color;
    return 0;
}

static const struct luaL_Reg image_lib[] = {
        {"new",    new_image},
        {NULL, NULL},
};

static const struct luaL_Reg image_meth[] = {
        {"width",  get_image_width},
        {"height", get_image_height},
        {"get",    get_image_pixel},
        {"set",    set_image_pixel},
        {NULL, NULL},
};

static int image_gc (lua_State *L) {
    ImageRgba *p = to_image(L);
    free_image(p);
    return 0;
}

static const luaL_Reg image_metameth[] = {
        {"__gc", image_gc},
        {NULL, NULL}
};

int luaopen_image(lua_State *L) {
    luaL_newlib(L, image_lib);
    luaL_newmetatable(L, YLN_IMAGE);  // metatable for file handles
    luaL_setfuncs(L, image_metameth, 0);  // add metamethods to new metatable
    luaL_newlib(L, image_meth);
    lua_setfield(L, -2, "__index");  /* metatable.__index = method table */
    lua_pop(L, 1);  // pop metatable
    return 1;
}
