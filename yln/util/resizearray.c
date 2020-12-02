//
// Created by philip on 30.11.20.
//

#include <assert.h>
#include "resizearray.h"

static size_t get_new_capacity(const ResizeArray *array, size_t required_capacity) {
    size_t increment = required_capacity / 2;
    if (increment < 15) {
        increment = 15;
    } else if (increment > 255) {
        increment = 255;
    }
    return required_capacity + increment;
}

inline void *array_get_ptr(const ResizeArray *array, size_t sizeof_item, int index) {
    assert(array != NULL);
    assert(index < array->size);
    assert(sizeof_item == array->sizeof_item);
    return &((byte *)array->items)[sizeof_item * index];
}

void array_init_impl(ResizeArray *array, size_t sizeof_item, int initial_capacity) {
    assert(array != NULL);
    assert(sizeof_item > 0);
    assert(initial_capacity >= 0);
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
        size_t new_capacity = get_new_capacity(array, index + 1);
        void *new_items = calloc(new_capacity, sizeof_item);
        if (array->items != NULL) {
            memcpy(new_items, array->items, array->size * sizeof_item);
            free(array->items);
        }
        array->items = new_items;
        array->capacity = new_capacity;
    }
    if (array->size <= index) {
        array->size = index + 1;
    }
    return array_get_ptr(array, sizeof_item, index);
}

inline void array_clear(ResizeArray *array) {
    assert(array != NULL);
    array->size = 0;
}

void array_remove(ResizeArray *array, int index) {
    assert(array != NULL);
    assert(index < array->size);
    assert(index >= 0);
    if (index < array->size - 1) {
        byte *dest = &((byte *) array->items)[index * array->sizeof_item];
        memmove(dest, dest + array->sizeof_item, array->sizeof_item * (array->size - index));
    }
    array->size--;
}

void *array_pop_impl(ResizeArray *array, size_t sizeof_item) {
    assert(array != NULL);
    int index = (int)array->size - 1;
    void *item_ptr = array_get_ptr(array, sizeof_item, index);
    array_remove(array, index);
    return item_ptr;
}
