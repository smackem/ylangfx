function basics()
    img = image.new(120, 240)
    print(img:width())
    print(img:height())
    img:set(10, 11, 123)
    return img:get(10, 11)
end

return basics()
