//
// Created by Philip Boger on 03.12.20.
//

#ifndef YLN_LUARGBA_H
#define YLN_LUARGBA_H

#ifdef __cplusplus
extern "C" {
#include <lua.h>
}
#else
#include <lua.h>
#endif

int luaopen_rgba(lua_State *L);

#endif //YLN_LUARGBA_H
