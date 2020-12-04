function trace(s)
    io.stderr:write(s)
end

function rgbaStr(color)
    return string.format("#%02x%02x%02x@%02x", rgba.r(color), rgba.g(color), rgba.b(color), rgba.a(color))
end

function randomCh()
    return math.random(0, 255)
end

function createRandomImage(width, height)
    img = image.new(width, height)
    for y = 1, height do
        for x = 1, width do
            local color = rgba.rgb(randomCh(), randomCh(), randomCh())
            img:set(x, y, color)
        end
    end
    return img
end

function traceImage(img)
    for y = 1, img:height() do
        for x = 1, img:width() do
            trace(rgba.str(img:get(x, y)))
            trace(" ")
        end
        trace("\n")
    end
    return img
end

local gaussian = kernel.of(5, 5, {
    0, 1, 2, 1, 0,
    1, 2, 4, 2, 1,
    2, 4, 8, 4, 2,
    1, 2, 4, 2, 1,
    0, 1, 2, 1, 0})
local laplace = kernel.of(3, 3, {
    -1, -2, -1,
     0,  0,  0,
     1,  2,  1})

return inp:convolve(gaussian):convolve(laplace)
