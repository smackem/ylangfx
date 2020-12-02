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

int main(int argc, char **argv) {
    test_resize_array();
    if (argc < 2) {
        TRACE("USAGE: yln_test LUA_FILE\n");
        return 1;
    }
    run_lua_script(argv[1]);
    return 0;
}