//
// Created by philip on 26.11.20.
//

#include <time.h>
#include "types.h"
#include "util.h"

long current_millis() {
    struct timespec ts;
    timespec_get(&ts, TIME_UTC);
    return ts.tv_sec * 1000 + ts.tv_nsec / (1000 * 1000);
}

bool str_endswith(const char *str, const char *end) {
    size_t end_len = strlen(end);
    size_t str_len = strlen(str);
    if (str_len < end_len) {
        return false;
    }
    return strcmp(str + str_len - end_len, end) == 0;
}

void replace_extension(char *dest, size_t dest_length, const char *path, const char *extension) {
    strncpy(dest, path, dest_length);
    dest[dest_length - 1] = 0;
    char *end = strrchr(dest, '.');
    if (end == NULL) {
        end = &dest[strlen(dest)];
    }
    strncpy(end, extension, dest_length - (end - dest));
    dest[dest_length - 1] = 0;
}

static void func() {
    ResizeArray array;
    array_init(&array, int, 16);
    for (int i = 0; i < 10; i++) {
        array_push(&array, int, i);
    }
    int x = array_get(&array, int, 4);
    array_set(&array, int, 4, x + 1);
    int *p = array.items;
    for (int i = 0; i < array.size; i++) {
        int v = array_get(&array,  int, i);
    }
    array_free(&array);
}
