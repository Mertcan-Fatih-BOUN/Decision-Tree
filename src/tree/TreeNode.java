package tree;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class TreeNode {
    /**
     * im0-im30000 64x64 images
     * im0 is complete black 64x64 image
     */
    static String thumbnail_folder = "D:\\Users\\Fatih\\Downloads\\mirflickr\\mirflickr25k\\combined\\";
    int single_width = 64 * 3 + 20;
    static int max_depth = 0;
    static int width = 0;
    static int height = 0;
    public int[] ids = new int[]{0, 0, 0};
    public TreeNode leftTreeNode;
    public TreeNode rightTreeNode;

    public void printToFile(String filename) throws IOException {
        max_depth = 0;
        find_max_depth(0);
        height = 64 + 128 * (max_depth + 1);

        int count = (int) Math.pow(2, max_depth);

        int gap = 64;
        width = count * single_width + (count + 2) * gap;
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

    private void add_images(Graphics graphics, int i, int x) throws IOException {
        BufferedImage[] images = new BufferedImage[3];
        images[0] = ImageIO.read(new File(thumbnail_folder + "im" + ids[0] + ".jpg"));
        images[1] = ImageIO.read(new File(thumbnail_folder + "im" + ids[1] + ".jpg"));
        images[2] = ImageIO.read(new File(thumbnail_folder + "im" + ids[2] + ".jpg"));

        int y = 128 * i + 64;
        graphics.drawImage(images[0], x - 106, y, null);
        graphics.drawImage(images[1], x - 32, y, null);
        graphics.drawImage(images[2], x + 42, y, null);

        if (leftTreeNode != null) {
            int below_count = (int) Math.pow(2, i + 1);
            int below_gap = (width - below_count  * single_width) / (below_count );
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setColor(Color.BLUE);
            graphics2D.setStroke(new BasicStroke(5));
            graphics2D.drawLine(x,y+64, x - below_gap/2 - single_width/2, y +128 );
            graphics2D.drawLine(x,y+64, x + below_gap/2 + single_width/2, y + 128 );
            leftTreeNode.add_images(graphics, i + 1, x - below_gap/2 - single_width/2);
            rightTreeNode.add_images(graphics, i + 1,  x + below_gap/2 + single_width/2);
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
