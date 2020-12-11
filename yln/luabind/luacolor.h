//
// Created by philip on 10.12.20.
//

#ifndef YLN_LUACOLOR_H
#define YLN_LUACOLOR_H

#ifdef __cplusplus
extern "C" {
#include <lua.h>
}
#else
#include <lua.h>
#endif

#include <imaging.h>

/**
 * The name of the lua color type
 */
#define YLN_COLOR "Yln.color"

Color *to_color(lua_State *L, int arg);
int luaopen_color(lua_State *L);
Color *push_new_color(lua_State *L);
void push_color(lua_State *L, const Color *color);

#endif //YLN_LUACOLOR_H
