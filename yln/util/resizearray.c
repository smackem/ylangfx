//
// Created by philip on 30.11.20.
//

#include <assert.h>
#include "resizearray.h"

static size_t get_new_capacity(const ResizeArray *array, size_t required_capacity) {
    size_t increment = required_capacity / 2;
    if (increment < 16) {
        increment = 16;
    } else if (increment > 256) {
        increment = 256;
    }
    return required_capacity + increment;
}

void *array_get_ptr(const ResizeArray *array, size_t sizeof_item, int index) {
    assert(array != NULL);
    assert(index < array->size);
    assert(sizeof_item == array->sizeof_item);
    return &((byte *)array->items)[sizeof_item * index];
}

void array_init_impl(ResizeArray *array, size_t sizeof_item, int initial_capacity) {
    assert(array != NULL);
    assert(sizeof_item > 0);
    bzero(array, sizeof(ResizeArray));
    array->sizeof_item = sizeof_item;
    array->capacity = initial_capacity;
    if (initial_capacity > 0) {
        array->items = calloc(initial_capacity, sizeof_item);
    }
}

void array_free(ResizeArray *array) {
    assert(array != NULL);
    if (array->items != NULL) {
        free(array->items);
    }
    bzero(array, sizeof(ResizeArray));
}

void *array_get_ptr_grow(ResizeArray *array, size_t sizeof_item, int index) {
    assert(array != NULL);
    assert(sizeof_item == array->sizeof_item);
    if (array->capacity <= index) {
        if (array->items != NULL) {
            free(array->items);
        }
        size_t new_capacity = get_new_capacity(array, index + 1);
        void *new_items = calloc(new_capacity, sizeof_item);
        array->items = new_items;
    }
    if (array->size <= index) {
        array->size = index + 1;
    }
    return array_get_ptr(array, sizeof_item, index);
}

void array_clear(ResizeArray *array) {
    assert(array != NULL);
    array->size = 0;
}
