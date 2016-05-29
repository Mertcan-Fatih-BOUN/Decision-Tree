package BuddingTreeMultiClass;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static BuddingTreeMultiClass.Runner.similar_count;

public class TreeNode {
    /**
     * im0-im30000 64x64 images
     * im0 is complete black 64x64 image
     */
//    static String thumbnail_folder = "D:\\Users\\Fatih\\Downloads\\mirflickr\\mirflickr25k\\combined\\";
    static String thumbnail_folder = "combined_thumbs\\";

    static final int image_width = 64;
    static final int image_height = 64;
    static final int class_bar_height = 20;
    static final int instance_width = image_width;
    static final int instance_height = image_height + 2 * class_bar_height;
    static final int instance_count = similar_count;
    static final int instance_gap = 10;
    static final int instance_group_width = (similar_count - 1) * instance_gap + similar_count * instance_width;
    static final int instance_group_height = instance_height;
    static final int instance_group_gap = 64;
    static final int level_gap = 70;

    static int max_depth = 0;
    static int width = 0;
    static int height = 0;
    public Instance[] instances;
    public TreeNode leftTreeNode;
    public TreeNode rightTreeNode;

    public Node node;

    public TreeNode() {
        Instance instance = new Instance();
        instance.mirflicker_id = 0;
        instance.r = new int[38];
        Arrays.fill(instance.r, 0);
        instances = new Instance[instance_count];
        for (int i = 0; i < instance_count; i++)
            instances[i] = instance;
    }

    public void printToFile(String filename) throws IOException {
        max_depth = 0;
        find_max_depth(0);

        height = level_gap + (max_depth + 1) * (level_gap + instance_group_height);
        int count = (int) Math.pow(2, max_depth);
        width = instance_group_gap + count * (instance_group_width + instance_group_gap);

        byte[] bytes = new byte[height * width * 3];
        Arrays.fill(bytes, (byte) 255);
        BufferedImage img = createRGBImage(bytes, width, height);

        add_images(img.getGraphics(), 0, width / 2);

        File outfile = new File(filename);
        ImageIO.write(img, "png", outfile);
    }

    private static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null), false, null);
    }

    private void add_images(Graphics graphics, int level, int x) throws IOException {
        BufferedImage[] images = new BufferedImage[instance_count];
//        System.out.println(instances[3].mirflicker_id);
        for (int i = 0; i < images.length; i++)
            images[i] = ImageIO.read(new File(thumbnail_folder + "im" + instances[i].mirflicker_id + ".jpg"));

        int y = level_gap + level * (level_gap + instance_group_height);

        int r = node.rho.length;

        int local_class_x = x - r / 2;

        for (int i = -1; i <= r; i++) {
            if (i == -1 || i == r)
                graphics.setColor(Color.blue);
            else {
                graphics.setColor(new Color(node.scaled_rho[i], node.scaled_rho[i], node.scaled_rho[i]));
            }
            graphics.drawRect(local_class_x + i, y, 1, class_bar_height);
        }


        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 10));

        int local_x = x - instance_group_width / 2;
        for (int i = 0; i < images.length; i++) {
            graphics.drawImage(images[i], local_x + i * (instance_width + instance_gap), y + class_bar_height, null);

            int count_class = 0;
            String s = "";
            for (int c = -1; c <= instances[i].r.length; c++) {
                if (c == -1 || c == instances[i].r.length)
                    graphics.setColor(Color.blue);
                else if (instances[i].r[c] == 0)
                    graphics.setColor(Color.black);
                else {
                    s += c + " ";
                    graphics.setColor(Color.blue);
                    graphics.drawString(s, local_x + i * (instance_width + instance_gap) + 40 + (count_class / 3) * 15,10 + y + class_bar_height + image_height + (count_class % 3) * 10);
                    s = "";
                    count_class++;
                    graphics.setColor(Color.white);
                }

                graphics.drawRect(local_x + i * (instance_width + instance_gap) + c, y + class_bar_height + image_height, 1, class_bar_height);

            }
        }




//        graphics.setFont(graphics.getFont().deriveFont(15f));

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 15));

        for(int i = 0; i < node.max_g_indexes.length; i++){
            String s = String.format("  %3d %.2f %.2f %.2f %s", node.max_g_indexes[i], node.max_g_values[i], node.max_g_values[i] - node.total_decision[node.max_g_indexes[i]], node.total_decision[node.max_g_indexes[i]], SetReader.POTENTIAL_LABELS[node.max_g_indexes[i]]);
            graphics.drawString(s, local_x + (images.length - 1) * (instance_width + instance_gap) + image_width + 10,10 + y + class_bar_height + i * 15);
        }

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 60));
        for(int i = 0; i < SetReader.POTENTIAL_LABELS.length; i++){
            String s = String.format("%2d %s", i, SetReader.POTENTIAL_LABELS[i]);
            graphics.drawString(s, 10 + (i/19) * 350, (i % 19) * 60 + 60);
        }




        if (leftTreeNode != null)

        {
            int below_count = (int) Math.pow(2, level + 1);
            int below_gap = (width - below_count * instance_group_width) / (below_count);
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setColor(Color.BLUE);
            graphics2D.setStroke(new BasicStroke((float) (10 * (1 - node.gama))));
            graphics2D.drawLine(x, y + instance_group_height, x - below_gap / 2 - instance_group_width / 2, y + instance_group_height + level_gap);
            graphics2D.drawLine(x, y + instance_group_height, x + below_gap / 2 + instance_group_width / 2, y + instance_group_height + level_gap);
            graphics2D.setStroke(new BasicStroke(0));
            leftTreeNode.add_images(graphics, level + 1, x - below_gap / 2 - instance_group_width / 2);
            rightTreeNode.add_images(graphics, level + 1, x + below_gap / 2 + instance_group_width / 2);
        }

    }

    private void find_max_depth(int i) {
        if (i > max_depth)
            max_depth = i;
        if (leftTreeNode != null)
            leftTreeNode.find_max_depth(i + 1);
        if (rightTreeNode != null)
            rightTreeNode.find_max_depth(i + 1);
    }


}
