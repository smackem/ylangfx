#include <wchar.h>
#include <build_config.h>
#include <imaging.h>
#include <time.h>
#include "imageio.h"

static void smoothenImage(ImageRgba *pDest, const ImageRgba *pOrig, int radius) {
    Kernel kernel;
    i32 diameter = radius * 2 + 1;
    initKernel(&kernel, diameter, diameter, 1);
    convolveImage(pDest, pOrig, &kernel);
    freeKernel(&kernel);
}

static long currentMillis() {
    struct timespec ts;
    timespec_get(&ts, TIME_UTC);
    return ts.tv_sec * 1000 + ts.tv_nsec / (1000 * 1000);
}

int main(int argc, char **argv) {
    ImageRgba orig;
    bzero(&orig, sizeof(orig));
    ImageRgba dest;
    bzero(&dest, sizeof(dest));
    error err = 0;
    wprintf(L"yln v%d.%d\n", yln_VERSION_MAJOR, yln_VERSION_MINOR);
    do {
        if (argc < 2) {
            wprintf(L"USAGE: yln IMAGE_FILE\n");
            break;
        }
        err = loadImage(&orig, argv[1]);
        if (err != OK) {
            break;
        }
        invertImage(&orig);
        cloneImage(&dest, &orig);
        long start = currentMillis();
        smoothenImage(&dest, &orig, 11);
        long end = currentMillis();
        wprintf(L"elapsed: %Ld ms", end - start);
        err = saveImage(&dest, argv[1]);
        if (err != OK) {
            break;
        }
    } once;
    freeImage(&orig);
    freeImage(&dest);
    return err;
}
