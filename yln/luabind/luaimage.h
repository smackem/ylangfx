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

int luaopen_image(lua_State *L);

#endif //YLN_LUAIMAGE_H
