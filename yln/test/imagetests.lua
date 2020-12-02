function basics()
    img = image.new(120, 240)
    print(image.width(img))
    print(image.height(img))
    
    return image.at(img, 10, 11)
end

return basics()
