#include <wchar.h>
#include <build_config.h>
#include <limits.h>
#include <imaging.h>
#include <util.h>

static void smoothen_image(ImageRgba *dest, const ImageRgba *orig, int radius) {
    Kernel kernel;
    int diameter = radius * 2 + 1;
    init_kernel(&kernel, diameter, diameter, 1);
    convolve_image(dest, orig, &kernel);
    free_kernel(&kernel);
}

static error load_image_from_file(ImageRgba *image, const char *path) {
    if (str_endswith(path, ".png")) {
        return load_png(image, path);
    }
    return load_image(image, path);
}

static error save_image_to_file(const ImageRgba *image, const char *path) {
    if (str_endswith(path, ".png")) {
        return save_png(image, path);
    }
    return save_image(image, path);
}

int main(int argc, char **argv) {
    ImageRgba orig;
    ImageRgba dest;
    error err = 0;
    char dest_path[PATH_MAX];
    ZERO(orig);
    ZERO(dest);

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

        err = load_image_from_file(&orig, argv[1]);
        if (err != OK) {
            break;
        }

        //invert_image(&orig);
        clone_image(&dest, &orig);
        long start = current_millis();
        smoothen_image(&dest, &orig, 3);
        long end = current_millis();
        wprintf(L"elapsed: %Ld ms\n", end - start);

        replace_extension(dest_path, PATH_MAX, argv[1], ".yli.png");
        err = save_image_to_file(&dest, dest_path);
    } ONCE;

    free_image(&orig);
    free_image(&dest);
    return err;
}
