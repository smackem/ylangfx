//
// Created by philip on 02.12.20.
//

#include <image.h>
#include <lauxlib.h>
#include <lualib.h>
#include <luaconf.h>
#include "luakernel.h"
#include "luaimage.h"

typedef struct image_rgba_wrapper {
    ImageRgba base;
    /**
     * if `true`, the image is subject to lua garbage collection. must be set if the image is created
     * from within a lua script. if the image is passed into the script from the C environment,
     * must be set to `false`.
     */
    bool managed;
} ImageRgbaWrapper;

static ImageRgbaWrapper *push_image_internal(lua_State *L, bool managed) {
    ImageRgbaWrapper *lua_image = (ImageRgbaWrapper *)lua_newuserdata(L, sizeof(ImageRgbaWrapper));
    luaL_setmetatable(L, YLN_IMAGE);
    lua_image->managed = managed;
    return lua_image;
}

static ImageRgba *push_new_image(lua_State *L, int width, int height) {
    ImageRgbaWrapper *lua_image = push_image_internal(L, true);
    init_image(&lua_image->base, width, height);
    return &lua_image->base;
}

static int new_image(lua_State *L) {
    int width = luaL_checkinteger(L, 1);
    int height = luaL_checkinteger(L, 2);
    luaL_argcheck(L, width > 0, 1, "width must be positive");
    luaL_argcheck(L, height > 0, 2, "height must be positive");
    push_new_image(L, width, height);
    return 1;
}

static int push_image_width(lua_State *L) {
    lua_pushinteger(L, to_image(L, 1)->width);
    return 1;
}

static int push_image_height(lua_State *L) {
    lua_pushinteger(L, to_image(L, 1)->height);
    return 1;
}

static rgba *get_image_pixel_addr(lua_State *L) {
    ImageRgba *image = to_image(L, 1);
    lua_Integer x = luaL_checkinteger(L, 2) - 1; // ranges from 1..width] as usual in lua
    lua_Integer y = luaL_checkinteger(L, 3) - 1;
    luaL_argcheck(L, image->pixels != NULL, 1, "image is uninitialized");
    luaL_argcheck(L, 0 <= x && x < image->width, 2, "x is out of range");
    luaL_argcheck(L, 0 <= y && y < image->height, 3, "y is out of range");
    return &image->pixels[y * image->width + x];
}

static int push_image_pixel(lua_State *L) {
    lua_pushinteger(L, *get_image_pixel_addr(L));
    return 1;
}

static int set_image_pixel(lua_State *L) {
    rgba *pixel_ptr = get_image_pixel_addr(L);
    int color = luaL_checkinteger(L, 4);
    *pixel_ptr = color;
    return 0;
}

static int push_convolve_image_pixel(lua_State *L) {
    const ImageRgba *image = to_image(L, 1);
    lua_Integer x = luaL_checkinteger(L, 2) - 1; // ranges from 1..width] as usual in lua
    lua_Integer y = luaL_checkinteger(L, 3) - 1;
    luaL_argcheck(L, 0 <= x && x < image->width, 2, "x is out of range");
    luaL_argcheck(L, 0 <= y && y < image->height, 3, "y is out of range");
    const Kernel *kernel = to_kernel(L, 4);
    lua_pushinteger(L, convolve_image_pixel(image, kernel, x, y));
    return 1;
}

static int push_convolve_image(lua_State *L) {
    const ImageRgba *image = to_image(L, 1);
    const Kernel *kernel = to_kernel(L, 2);
    ImageRgba *dest = push_new_image(L, image->width, image->height);
    convolve_image(dest, image, kernel);
    return 1;
}

static const struct luaL_Reg function_lib[] = {
        {"new",    new_image},
        {NULL, NULL},
};

static const struct luaL_Reg method_lib[] = {
        {"width",      push_image_width},
        {"height",     push_image_height},
        {"get",        push_image_pixel},
        {"set",        set_image_pixel},
        {"convolve",   push_convolve_image},
        {"convolveAt", push_convolve_image_pixel},
        {NULL, NULL},
};

static int do_gc (lua_State *L) {
    ImageRgbaWrapper *p = (ImageRgbaWrapper *) to_image(L, 1);
    if (p->managed) {
        free_image(&p->base);
    } else {
        p->base.width = 0;
        p->base.height = 0;
    }
    return 0;
}

static const luaL_Reg metameth_lib[] = {
        {"__gc", do_gc},
        {NULL, NULL}
};

ImageRgba *to_image(lua_State *L, int arg) {
    void *userdata = luaL_checkudata(L, arg, YLN_IMAGE);
    luaL_argcheck(L, userdata != NULL, arg, "`image` expected");
    return (ImageRgba *)userdata;
}

void push_image(lua_State *L, const ImageRgba *image) {
    struct image_rgba_wrapper *lua_image = push_image_internal(L, false);
    lua_image->base = *image;
}

int luaopen_image(lua_State *L) {
    luaL_newlib(L, function_lib);
    luaL_newmetatable(L, YLN_IMAGE);  // metatable for file handles
    luaL_setfuncs(L, metameth_lib, 0);  // add metamethods to new metatable
    luaL_newlib(L, method_lib);
    lua_setfield(L, -2, "__index");  /* metatable.__index = method table */
    lua_pop(L, 1);  // pop metatable
    return 1;
}
