//
// Created by philip on 02.12.20.
//

#ifndef YLN_LUAIMAGE_H
#define YLN_LUAIMAGE_H

#ifdef __cplusplus
extern "C" {
#include <lua.h>
}
#else
#include <lua.h>
#endif

#include <imaging.h>

/**
 * The name of the lua image type
 */
#define YLN_IMAGE "Yln.image"

ImageFloat *to_image(lua_State *L, int arg);
int luaopen_image(lua_State *L);
void push_image(lua_State *L, const ImageFloat *image);

#endif //YLN_LUAIMAGE_H
