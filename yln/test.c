#include <limits.h>
#include <imaging.h>
#include <util.h>
#include <luabind.h>

static void report_array(const ResizeArray *array, const wchar_t *category) {
    wprintf(L"ResizeArray@%p [%ls]\n  size: %u\n  capacity: %u\n  sizeof_item: %u\n",
            array, category, array->size, array->capacity, array->sizeof_item);
    wprintf(L"  ");
    for (int i = 0; i < array->size; i++) {
        wprintf(L"%d ", array_get(array, int, i));
    }
    wprintf(L"\n");
}

static void test_resize_array() {
    ResizeArray array;
    array_init(&array, int, 0);
    report_array(&array, L"initialized");
    array_push(&array, int, 100);
    report_array(&array, L"pushed 1 item");
    array_push(&array, int, 101);
    report_array(&array, L"pushed 1 item");
    int popped = array_pop(&array, int);
    wprintf(L"popped %d\n", popped);
    report_array(&array, L"popped 1 item");
    for (int n = 0; n < 20; n++) {
        array_push(&array, int, n + 101);
    }
    report_array(&array, L"pushed 20 items");
    array_remove(&array, 0);
    report_array(&array, L"removed first item");
    array_clear(&array);
    report_array(&array, L"cleared");
    array_free(&array);
    report_array(&array, L"freed");
}

static void laplace(ImageRgba *dest, const ImageRgba *orig) {
    Kernel kernel;
    init_kernel(&kernel, 3, 3, -1.0);
    kernel.values[4] = (float)8.0;
    convolve_image(dest, orig, &kernel);
}

int main(int argc, char **argv) {
    ImageRgba orig;
    ImageRgba dest;
    error err = 0;
    char dest_path[PATH_MAX];
    zero(orig);
    zero(dest);

    do {
        if (argc < 3) {
            trace("USAGE: yln_test LUA_FILE PNG_FILE\n");
            break;
        }
        err = load_png(&orig, argv[2]);
        if (err != OK) {
            break;
        }
        err = run_lua_script(argv[1], &dest, &orig);
        if (err != OK) {
            break;
        }
//        init_image(&dest, orig.width, orig.height);
//        laplace(&dest, &orig);
        replace_extension(dest_path, PATH_MAX, argv[2], ".processed.png");
        err = save_png(&dest, dest_path);
        if (err != OK) {
            break;
        }
        trace("image written to %s\n", dest_path);
    } ONCE;

    return err;
}