//
// Created by philip on 26.11.20.
//

#ifndef YLN_UTIL_H
#define YLN_UTIL_H

#include "resizearray.h"

/**
 * Gets the milliseconds that have elapsed since some time in the past
 * (e.g. system boot time). Use this function for measurement of time spans.
 * @return the milliseconds that have elapsed since some time in the past.
 */
long current_millis();

/**
 * Determines whether the specified `str` end with `end.
 * @param str The string to test.
 * @param end The end to test against.
 * @return `true` if `str` ends with `end`.
 */
bool str_endswith(const char *str, const char *end);

/**
 * Replaces the file name extension of a path.
 * @param dest The destination buffer.
 * @param dest_length The length of the destination buffer.
 * @param path The path.
 * @param extension The new extension for `path`
 */
void replace_extension(char *dest, size_t dest_length, const char *path, const char *extension);

#endif //YLN_UTIL_H
