import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static javax.swing.BorderFactory.createLineBorder;

public class Startti2 {
    private JButton button1;
    private JLabel labelFilename;
    private JPanel panel2;
    private JLabel labelResult;
    private JLabel initialImg;
    private JLabel lblFix;
    private JLabel correctedImg;
    //private JFrame frame;
    private boolean transparency = false;

    public Startti2() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = "";
                String status = "Initial";
                try {
                    String userDir = System.getProperty("user.home");
                    final JFileChooser fc = new JFileChooser(userDir + "/trans");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "png", "gif");
                    fc.addChoosableFileFilter(filter);

                    int result = fc.showSaveDialog(null);
                    int response = fc.showOpenDialog(fc);

                    if (response == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fc.getSelectedFile();
                        String path = selectedFile.getAbsolutePath();
                        //toLowerCase used to identify that file is initial..
                        fileName = selectedFile.toString().toLowerCase(Locale.ROOT);
                        initialImg.setIcon(ResizeImage(path, status));

                        //Check transparency
                        try {
                            BufferedImage image;
                            image = ImageIO.read(new File(fileName));
                            System.out.println("File in BufferedReader");

                            //iterate over pixels.
                            for (int i = 0; i < image.getWidth(); i++) {
                                for (int j = 0; j < image.getHeight(); j++) {

                                    int pixel = image.getRGB(i, j);
                                    if (pixel >>> 24 == 0x00) {
                                        transparency = true;

                                    } else {
                                        transparency = false;
                                    }
                                }
                            }

                            //Verdict
                            labelResult.setOpaque(true);
                            labelResult.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
                            //labelResult.setBounds(600, 600, 300, 100);

                            if (transparency) {
                                System.out.println("Filename: " + fileName);
                                labelResult.setBackground(Color.red);
                                labelResult.setText(fileName + " is transparent");

                                int resp = JOptionPane.showConfirmDialog(panel2, "Yes to fix, no to exit", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                if (resp == JOptionPane.YES_OPTION) {
                                    try {
                                        System.out.println("Starting to fix transparent image");

                                        //Delete existing file if any.
                                        File fName = new File(fileName);
                                        File newDir = new File("C:/Users/jra/trans/Fixed/");
                                        File completePath = new File("C:/Users/jra/trans/Fixed/" + fName.getName());

                                        if (completePath.exists()) {
                                            completePath.delete();
                                            System.out.println("File deleted from Fixed directory");

                                        }

                                        BufferedImage transImg = colorImage(ImageIO.read(new File(fileName)));
                                        ImageIO.write(transImg, "png", completePath);

                                        //Showing image
                                        correctedImg.setIcon(ResizeImage(completePath.toString(), "fixed"));

                                        //check differences...
                                        compare(image, transImg, labelResult);

                                    } catch (IOException writeE) {
                                        System.out.println(writeE.getStackTrace());
                                    }

                                } else if (resp == JOptionPane.NO_OPTION) {
                                    System.out.println("No selected, nothing to be done");
                                    //System.exit(0);
                                }

                            } else {

                                labelResult.setBackground(Color.green);
                                labelResult.setText(fileName + " is not Transparent");
                            }

                        } catch (IOException exp) {
                            System.out.println("" + exp.getMessage());
                        }
                    } else {
                        labelResult.setText("User cancelled");
                    }
                } catch (Exception exp) {
                    System.out.println("" + exp.getMessage());
                }
            }
        });
    }

    public static void compare(BufferedImage image, BufferedImage transImg, JLabel labelResult) throws Exception {
        BufferedImage img1 = image;
        BufferedImage img2 = transImg;
        int w1 = img1.getWidth();
        int w2 = img2.getWidth();
        int h1 = img1.getHeight();
        int h2 = img2.getHeight();
        if ((w1 != w2) || (h1 != h2)) {
            System.out.println("Both images should have same dimwnsions");
        } else {
            long diff = 0;
            for (int j = 0; j < h1; j++) {
                for (int i = 0; i < w1; i++) {
                    //Getting the RGB values of a pixel
                    int pixel1 = img1.getRGB(i, j);
                    Color color1 = new Color(pixel1, true);
                    int r1 = color1.getRed();
                    int g1 = color1.getGreen();
                    int b1 = color1.getBlue();
                    int pixel2 = img2.getRGB(i, j);
                    Color color2 = new Color(pixel2, true);
                    int r2 = color2.getRed();
                    int g2 = color2.getGreen();
                    int b2 = color2.getBlue();
                    //sum of differences of RGB values of the two images
                    long data = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                    diff = diff + data;
                }
            }
            double avg = diff / (w1 * h1 * 3);
            double percentage = (avg / 255) * 100;
            System.out.println("Difference: " + percentage);
            if (percentage != 0.0) {
                labelResult.setText("Differences with initial and fixed one");
            } else {
                labelResult.setText("Fix didn't do anything....");
            }
        }
    }


    public ImageIcon ResizeImage(String ImagePath, String status) {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        MyImage.setDescription("Image:" + ImagePath + ", initial / after: " + status);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(300, 400, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }

    private static BufferedImage colorImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        // Fix image if having alpha parts
        if (image.getTransparency() == Transparency.TRANSLUCENT) {
            //Fill non parency parts
            Graphics2D graphics = image.createGraphics();
            try {
                graphics.setComposite(AlphaComposite.DstOver); // Set composite rules to paint "behind"
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            } finally {
                graphics.dispose();
            }
        }

        return image;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Startti");
        frame.setBounds(600, 200, 600, 600);
        frame.setContentPane(new Startti2().panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
