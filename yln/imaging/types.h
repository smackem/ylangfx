//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_TYPES_H
#define YLN_TYPES_H

#include <wchar.h>
#include <stdlib.h>
#include <memory.h>

#define newobj(type) ((type *)calloc(sizeof(type), 1))
#define newarr(type, size) ((type *)calloc(sizeof(type), size))

typedef int i32;
typedef unsigned int u32;
typedef unsigned char byte;
typedef unsigned int rgba;
typedef int error;
typedef wchar_t *wstr;
typedef const wchar_t *cwstr;
typedef char *str;
typedef const char *cstr;

typedef byte bool;
#define true (1)
#define false (0)

#endif //YLN_TYPES_H
