#include <wchar.h>
#include <build_config.h>
#include <imaging.h>

int main(int argc, char **argv) {
    ImageRgba image;
    bzero(&image, sizeof(image));
    error err = 0;
    wprintf(L"yln v%d.%d\n", yln_VERSION_MAJOR, yln_VERSION_MINOR);
    do {
        if (argc < 2) {
            wprintf(L"USAGE: yln IMAGE_FILE\n");
            break;
        }
        err = loadImage(&image, argv[1]);
        if (err != OK) {
            break;
        }
        invertImage(&image);
        err = saveImage(&image, argv[1]);
        if (err != OK) {
            break;
        }
    } ONCE;
    freeImage(&image);
    return err;
}
