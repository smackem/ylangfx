//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_TYPES_H
#define YLN_TYPES_H

#include <wchar.h>
#include <stdlib.h>
#include <memory.h>
#include <stdbool.h>

#define newobj(type) ((type *)calloc(sizeof(type), 1))
#define newarr(type, size) ((type *)calloc(sizeof(type), size))

#define once while (0)

typedef int i32;
typedef unsigned int u32;
typedef unsigned char byte;
typedef unsigned int rgba;
typedef wchar_t *wstr;
typedef const wchar_t *cwstr;
typedef char *str;
typedef const char *cstr;

typedef int error;
#define OK (0)

#endif //YLN_TYPES_H
