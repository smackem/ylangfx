//
// Created by philip on 30.11.20.
//

#ifndef YLN_RESIZEARRAY_H
#define YLN_RESIZEARRAY_H

#include "types.h"

typedef struct resize_array {
    size_t size;
    size_t capacity;
    size_t sizeof_item;
    void *items;
} ResizeArray;

void *array_get_ptr(const ResizeArray *array, size_t sizeof_item, int index);
#define array_get(array_ptr, type, index) (*(type *) array_get_ptr(array_ptr, sizeof(type), index))
#define array_set(array_ptr, type, index, item) (*(type *) array_get_ptr(array_ptr, sizeof(type), index) = item)

void array_init_impl(ResizeArray *array, size_t sizeof_item, int initial_capacity);
#define array_init(array_ptr, type, initial_capacity) (array_init_impl(array_ptr, sizeof(type), initial_capacity))

void array_free(ResizeArray *array);

void *array_get_ptr_grow(ResizeArray *array, size_t sizeof_item, int index);
#define array_push(array_ptr, type, item) (*(type *) array_get_ptr_grow(array_ptr, sizeof(type), (array_ptr)->size) = item)

void array_clear(ResizeArray *array);

void array_remove_impl(ResizeArray *array, size_t sizeof_item, int index);
#define array_remove(array_ptr, type, index) (array_remove_impl(array_ptr, sizeof(type), index))

#endif //YLN_RESIZEARRAY_H
