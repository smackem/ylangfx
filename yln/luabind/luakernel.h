//
// Created by Philip Boger on 03.12.20.
//

#ifndef YLN_LUAKERNEL_H
#define YLN_LUAKERNEL_H

#ifdef __cplusplus
extern "C" {
#include <lua.h>
}
#else
#include <lua.h>
#endif

#include <imaging.h>

/**
 * The name of the lua kernel type
 */
#define YLN_KERNEL "Yln.kernel"

Kernel *to_kernel(lua_State *L, int arg);
int luaopen_kernel(lua_State *L);

#endif //YLN_LUAKERNEL_H
