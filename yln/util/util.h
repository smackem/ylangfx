//
// Created by philip on 26.11.20.
//

#ifndef YLN_UTIL_H
#define YLN_UTIL_H

long current_millis();
bool str_endswith(const char *str, const char *end);
void replace_extension(char *dest, size_t dest_length, const char *path, const char *extension);

#endif //YLN_UTIL_H
