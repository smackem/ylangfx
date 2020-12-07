local function trace(s)
    io.stderr:write(s)
end

local function rgbaStr(color)
    return string.format("#%02x%02x%02x@%02x", rgba.r(color), rgba.g(color), rgba.b(color), rgba.a(color))
end

local function randomCh()
    return math.random(0, 255)
end

local function createRandomImage(width, height)
    local img = image.new(width, height)
    for y = 1, height do
        for x = 1, width do
            local color = rgba.rgb(randomCh(), randomCh(), randomCh())
            img:set(x, y, color)
        end
    end
    return img
end

local function traceImage(img)
    for y = 1, img:height() do
        for x = 1, img:width() do
            trace(rgba.str(img:get(x, y)))
            trace(" ")
        end
        trace("\n")
    end
    return img
end

local function traceKernel(k)
    for y = 1, k:height() do
        for x = 1, k:width() do
            trace(k:get(x, y))
            trace(" ")
        end
        trace("\n")
    end
end

local function makeGaussian(radius)
    local width = radius * 2 + 1
    local height = width
    local values = {}
    local r2 = radius * radius
    local sr = math.sqrt(radius)
    for y = 1, height do
        for x = 1, width do
            local distance = math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius))
            table.insert(values, math.exp(-(sr * distance * distance / r2)))
        end
    end
    return kernel.of(width, height, values)
end

local function makeLaplace(radius)
    local width = radius * 2 + 1
    local height = width
    local values = {}
    for i = 1, width * height do table.insert(values, -1) end
    values[math.ceil(#values / 2)] = width * height - 1
    return kernel.of(width, height, values)
end

local gaussian = makeGaussian(5)
local laplace = kernel.of(3, 3, {
    -1, -1, -1,
    -1,  8, -1,
    -1, -1, -1,
})
laplace = makeLaplace(2)

traceKernel(laplace)

return inp:convolve(gaussian)
    :convolve(laplace)
