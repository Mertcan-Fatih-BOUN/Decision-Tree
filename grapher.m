tag = 'log/37668/';

header =  tdfread(strcat(tag, 'header.txt'),' ');


gridinput =  tdfread(strcat(tag, 'grid-input.txt'),' ');

input = tdfread(strcat(tag, 'input.txt'),' ');

class_count = header.class_count;

indices = {};
for i = 1:class_count
    indices{i} = find(input.y == i-1);
end

res = header.res;


[xq,yq] = meshgrid(gridinput.x1,gridinput.x2);

figure
epoch = 0;
while 1
    if exist(strcat(tag, 'epoch-',  num2str(epoch), '.txt'), 'file')
        epoch = epoch +1;
    else
        break
    end
end

row = 4;
column = 5;
clear linspace;
linspace = linspace(0,epoch-1,row*column);
linspace = int64(linspace);

count = 1;

for i = linspace
    subplot(row,column,count)  
    count = count+1;
    s = tdfread(strcat(tag, 'epoch-',  num2str(i), '.txt'),' ');
    y = s.y;

    
    for j = 1:class_count
        scatter(input.x1(indices{j}),input.x2(indices{j}), '.' );
        hold on
    end
 
   
    y =  reshape(y, header.res, header.res);
    hold on

  [C,h] = contour(xq,yq,y,1:class_count);
  h.LineColor = 	[0 0 0];
end
