//
// Created by Philip Boger on 22.11.20.
//

#ifndef YLN_TYPES_H
#define YLN_TYPES_H

#include <wchar.h>
#include <stdlib.h>
#include <memory.h>
#include <stdbool.h>
#include <stdint.h>

#define newobj(type) ((type *)calloc(sizeof(type), 1))
#define newarr(type, size) ((type *)calloc(sizeof(type), size))

#define once while (0)

typedef uint8_t byte;
typedef uint32_t rgba;

typedef int error;
#define OK (0)

#endif //YLN_TYPES_H
