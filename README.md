# ylangfx
IDE for for ylang, an image-manipulation language

![screenshot1](https://github.com/smackem/ylangfx/blob/master/ylangfx-app/src/site/doc/screenshot1.png "Screenshot1")

# ylang version 2
ylang2 is the successor to the first version, which was hosted by a command-line utility written in Go (https://github.com/smackem/ylang).

The major advantages of ylang 2 over ylang 1 are the following:
* support for multiple images (not just one input and one output image)
* the input image can be adressed using the identifier `$in`:<br/>
  `return $in` is a complete ylang2 script that just returns the input image
* image pixels are adressed with the `[]` operator, not the `@` operator<br/>
  e.g. pixel inversion (ylang 1: `@pt = -@pt`) is now written `out[pt] = -inp[pt]`, where `inp` and `out` are two image instances
* kernels can be used like greyscale images
* highly optimized, optional C backend `yln`<br/>
  to make use of the C backend, compile yln using CMake for your platform and pass the directory containing the resulting library to ylangfx:
  `java -jar {PATH_TO_YLANGFX-APP.JAR} -Djava.library.path={DIR_CONTAINING_YLN}`
* ability to include library files
* a standard library with functions for morphological operations, filters and more image manipulation tools<br/>
  the standard library is included as source code and can be browsed with the Library Browser
* support for panics and assertions
* scripts can `return` objects of any type - not just images.<br/>
  if a script returns a list or map, all images and kernels contained in these objects will be displayed by the IDE
* functions can be adressed using the operator `@`:
  ```
  fn compare(a, b) {
    return a - b
  }
  [5, 1, 10].sort(@compare)
  ```
  lambda syntax is no longer supported.<br>
  also use the `@` operator to invoke function objects:
  ```
  fn apply_color_filter(img, filter) {
    out := img.clone()
    for p in img.bounds {
      out[p] = filter@(img[p])
    }
  }
  fn binarize(color) {
    return color.i01 > 0.5 ? #ffffff : #000000
  }
  return apply_color_filter($in, @binarize)
  ```
* better error messages from the compiler as well as from the execution engine, including stack traces
* a help system that displays all built-in types and methods

## How to build

- Install Java 15
- Install Maven 3.2 or higher
- Enter the following in your shell:

```
git clone https://github.com/smackem/ylangfx.git
cd ylangfx
mvn package
java -jar ylangfx-app/target/ylangfx-app-1.0-SNAPSHOT.jar
```

- This will open the IDE with some sample code in the editor - just load an image by clicking "Load Image..." and run the code.
