for i = 0:3:19
    figure
    s = tdfread(strcat('out', num2str(i), '.txt'),' ');

    indices0_0 = find(s.y ==0 & s.z == 0);
    indices1_0 = find(s.y ==1 & s.z == 0);
    indices0_1 = find(s.y ==0 & s.z == 1);
    indices1_1 = find(s.y ==1 & s.z == 1);

    scatter(s.x0(indices0_0),s.x1(indices0_0), 'd' ,'filled');
    hold on;
    scatter(s.x0(indices0_1),s.x1(indices0_1), 'd');
    hold on;
    scatter(s.x0(indices1_0),s.x1(indices1_0), 'o');
    hold on;
    scatter(s.x0(indices1_1),s.x1(indices1_1), 'o' ,'filled');

    indices0 = find(s.z ==0);
    indices1 = find(s.z ==1);

    [xq,yq] = meshgrid(-2.5:.01:2.5, -2.5:.01:2.5);
    vq = griddata(s.x0,s.x1,s.z,xq,yq);


    hold on
    contour(xq,yq,vq);
end


