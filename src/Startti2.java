import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static javax.swing.BorderFactory.createLineBorder;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Startti2 {
    private JButton button1;
    private JLabel labelFilename;
    private JPanel panel2;
    private JLabel labelResult;
    private JFrame frame;
    private boolean transparency = false;

    public Startti2() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String fileName = "";
                String currentPath = "";
                try {
                    currentPath = new java.io.File("C:\\Users\\jra\\OneDrive - Whitelake Software Point Oy\\Pictures\\trans\\").getCanonicalPath();

                    final JFileChooser fc = new JFileChooser(currentPath);
                    int response = fc.showOpenDialog(null);

                    if (response == JFileChooser.APPROVE_OPTION) {
                        fileName = fc.getSelectedFile().toString();
                        System.out.println("Filename selected " + fileName);
                    } else {
                        labelFilename.setText("User cancelled");
                    }
                    //Check transparency (seems that it's not working)
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
                        labelResult.setBounds(600, 600, 300, 100);

                        if (transparency) {
                            System.out.println("Filename: " + fileName);
                            labelFilename.setText(fileName);
                            labelResult.setBackground(Color.red);
                            labelResult.setText("Transparent");

                            int resp = JOptionPane.showConfirmDialog(panel2, "Yes to fix, no to exit", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (resp == JOptionPane.YES_OPTION) {
                                //Fixing transparent file
                                System.out.println("Starting to fix transparen image");

                                try {
                                    System.out.println("on try");
                                    BufferedImage img = colorImage(ImageIO.read(new File(fileName)));

                                    ImageIO.write(img, "png", new File(fileName.toUpperCase(Locale.ROOT)));
                                    System.out.println("Check new file");
                                } catch (IOException writeE) {
                                    System.out.println(writeE.getStackTrace());
                                }


                            } else if (resp == JOptionPane.NO_OPTION) {
                                System.out.println("No selected, nothing to be done");
                                //System.exit(0);
                            }

                        } else {
                            labelFilename.setText(fileName);
                            labelResult.setBackground(Color.green);
                            labelResult.setText("Not Transparent");
                        }

                    } catch (IOException exp) {
                        System.out.println("" + exp.getMessage());

                    }
                } catch (
                        IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private static BufferedImage colorImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = 0;
                pixels[1] = 0;
                pixels[2] = 255;
                raster.setPixel(xx, yy, pixels); // Set to white
            }
        }
        return image;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Startti");
        frame.setBounds(600, 200, 400, 200);
        frame.setContentPane(new Startti2().panel2);


        //frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
