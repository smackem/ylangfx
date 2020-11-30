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
#include <stdio.h>

#define NEW_OBJ(type) ((type *)calloc(sizeof(type), 1))
#define NEW_ARR(type, size) ((type *)calloc(size, sizeof(type)))
#define ZERO(item) bzero(&item, sizeof(item))
#define ONCE while (0)
#define TRACE(...) fprintf(stderr, __VA_ARGS__)

typedef uint8_t byte;

typedef int error;
#define OK (0)

#endif //YLN_TYPES_H
