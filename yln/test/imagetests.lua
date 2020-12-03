function trace(s)
    io.stderr:write(s)
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
            trace(img:get(x, y))
            trace(" ")
        end
        trace("\n")
    end
    return img
end

function basics()
    local img = createRandomImage(12, 8)
    traceImage(img)
    img:set(5, 5, 123)
    trace("\n")
    traceImage(img)
    return img:get(5, 5)
end

return basics()
