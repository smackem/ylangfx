#include <wchar.h>
#include <build_config.h>
#include <limits.h>
#include <imaging.h>
#include <util.h>

static error load_image_from_file(ImageRgba *image, const char *path) {
    return load_image(image, path);
}

static error save_image_to_file(const ImageRgba *image, const char *path) {
    return save_image(image, path);
}

int main(int argc, char **argv) {
    ImageRgba orig;
    ImageRgba dest;
    error err = 0;
    char dest_path[PATH_MAX];
    zero(orig);
    zero(dest);

    wprintf(L"yln v%d.%d\nsizeof int = %d bit | pointer = %d bit\n",
            yln_VERSION_MAJOR,
            yln_VERSION_MINOR,
            sizeof(int) * 8,
            sizeof(void *) * 8);

    do {
        if (argc < 2) {
            wprintf(L"USAGE: yln IMAGE_FILE\n");
            break;
        }

        err = load_image_from_file(&orig, argv[1]);
        if (err != OK) {
            break;
        }

        clone_image_rgba(&dest, &orig);
        long start = current_millis();
        invert_image_rgba(&orig);
        long end = current_millis();
        wprintf(L"elapsed: %Ld ms\n", end - start);

        replace_extension(dest_path, PATH_MAX, argv[1], ".yli.png");
        err = save_image_to_file(&dest, dest_path);
    } ONCE;

    free_image_rgba(&orig);
    free_image_rgba(&dest);
    return err;
}
