#include <wchar.h>
#include <build_config.h>
#include <imaging.h>
#include <time.h>
#include "imageio.h"

static void smoothenImage(ImageRgba *pDest, const ImageRgba *pOrig, int radius) {
    Kernel kernel;
    int diameter = radius * 2 + 1;
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

    wprintf(L"yln v%d.%d\nsizeof int = %d bit | pointer = %d bit\n",
            yln_VERSION_MAJOR,
            yln_VERSION_MINOR,
            sizeof(int) * 8,
            sizeof(void *) * 8);
    const rgba col = RGBA(0xff, 0x20, 0x10, 10.3);
    wprintf(L"red = %02x %02x %02x %02x\n", R(col), G(col), B(col), A(col));

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
        wprintf(L"elapsed: %Ld ms\n", end - start);
        err = saveImage(&dest, argv[1]);
        if (err != OK) {
            break;
        }
    } once;

    freeImage(&orig);
    freeImage(&dest);
    return err;
}
