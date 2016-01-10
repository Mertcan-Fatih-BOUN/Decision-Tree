weights =  dlmread(strcat('log/weights/', 'w1_70_0.01.txt'),' ');
nCol = size(weights,2);
weights(:,nCol)=[];
d = size(weights);
image_size = sqrtm(d(2));
number_of_images = d(1);
images = zeros((number_of_images / 10 + 1) * image_size, 10 * image_size);
for i = 1:d(1)
    image = weights(i,:);
    minm = min(image);
    maxm = max(image);
    image = reshape(image, [image_size, image_size]);
    image =  image - minm;
    image =  image ./ maxm ; 
    image =  image .* 255 ;
    images(floor((i - 1)/10) * image_size + 1: (floor((i-1)/10) + 1) * image_size, (mod(i - 1,10)) * image_size + 1: (mod(i - 1,10) + 1) * image_size) = image; 
end

imshow(images, [0 255]);
