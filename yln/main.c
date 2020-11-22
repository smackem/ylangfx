#include <stdio.h>
#include <unistd.h>
#include <wchar.h>
#include <build_config.h>
#include <imaging.h>

#define BUFFER_LEN (1024)

typedef unsigned char byte_t;

int main(int argc, char **argv) {
    wprintf(L"yln v%d.%d\n", yln_VERSION_MAJOR, yln_VERSION_MINOR);
    printIt();
    return 0;
}
