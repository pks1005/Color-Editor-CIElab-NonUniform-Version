import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class ImageLoader extends JPanel {

	public static BufferedImage img;
	public static WritableRaster wRaster;
	int width, height;
	Color vertLinesColor = new Color(140, 140, 140);
	Color horiLinesColor = new Color(180, 180, 180);

	public ImageLoader() {
		setLayout(null);
	}

	public void updateGraph(Graphics g) {
		int noOfObjects = ColorSequenceEditor.uvPointList.size();
		if (noOfObjects != 0) {
			int dx = (width - ColorSequenceEditor.lPad - ColorSequenceEditor.rPad) / noOfObjects;
			double dE, pre_dE = 0;

			g.setColor(Color.WHITE);
			g.drawRect(ColorSequenceEditor.lPad, ColorSequenceEditor.tPad, dx * noOfObjects, height - 1);
			g.setColor(Color.WHITE);
			g.drawString("Colormap Uniformity", width / 2 - 55, 15);
			g.setColor(Color.WHITE);
			g.drawString("dE", ColorSequenceEditor.lPad - 46, 316);
			g.setColor(horiLinesColor);
			for (int i = 0; i < 10; i++) {
				g.drawLine(ColorSequenceEditor.lPad + 1,
						ColorSequenceEditor.tPad + (height - ColorSequenceEditor.tPad) / 10 * i,
						ColorSequenceEditor.lPad - 1 + dx * noOfObjects,
						ColorSequenceEditor.tPad + (height - ColorSequenceEditor.tPad) / 10 * i);
			}
			g.setColor(Color.BLACK);

			ColorSequenceEditor.td = 0;
			if (noOfObjects > 1) {
				for (int i = 1; i < noOfObjects; i++) {
					dE = Math.sqrt(Math.pow(
							ColorSequenceEditor.uvPointList.get(i).l - ColorSequenceEditor.uvPointList.get(i - 1).l, 2)
							+ Math.pow((ColorSequenceEditor.uvPointList.get(i).u
									- ColorSequenceEditor.uvPointList.get(i - 1).u) * .15, 2)
							+ Math.pow((ColorSequenceEditor.uvPointList.get(i).v
									- ColorSequenceEditor.uvPointList.get(i - 1).v) * .15, 2));
					if (dE > pre_dE)
						pre_dE = dE;
					ColorSequenceEditor.td += dE;
				}
				int j = (int) (pre_dE / 10) + 1;
				g.setColor(Color.WHITE);
				for (int i = 0; i < 10; i++) {
					g.drawString("" + j * (10 - i), ColorSequenceEditor.lPad - 24,
							ColorSequenceEditor.tPad + (height - ColorSequenceEditor.tPad) / 10 * i + 5);
				}
				g.drawString("0", ColorSequenceEditor.lPad - 24, height);

				double scalingFactor = (height - ColorSequenceEditor.tPad) / j / 10;
				pre_dE = 0;
				if (ColorSequenceEditor.isUniform || !ColorSequenceEditor.isUniform) {
					for (int i = 1; i < noOfObjects; i++) {
						dE = Math
								.sqrt(Math
										.pow(ColorSequenceEditor.uvPointList.get(i).l
												- ColorSequenceEditor.uvPointList.get(i - 1).l, 2)
										+ Math.pow(
												(ColorSequenceEditor.uvPointList.get(i).u
														- ColorSequenceEditor.uvPointList.get(i - 1).u) * .15,
												2)
										+ Math.pow((ColorSequenceEditor.uvPointList.get(i).v
												- ColorSequenceEditor.uvPointList.get(i - 1).v) * .15, 2));

						g.setColor(vertLinesColor);
						g.drawLine(dx * i + ColorSequenceEditor.lPad, ColorSequenceEditor.tPad,
								dx * i + ColorSequenceEditor.lPad, height);
						g.setColor(Color.RED);
						g.fillOval(dx * i - 4 + ColorSequenceEditor.lPad, ColorSequenceEditor.tPad
								+ (int) ((height - ColorSequenceEditor.tPad) - dE * scalingFactor - 4), 8, 8);

						g.setColor(Color.WHITE);
						if (i > 1) {
							g.drawLine(dx * (i - 1) + ColorSequenceEditor.lPad,
									ColorSequenceEditor.tPad
											+ (int) ((height - ColorSequenceEditor.tPad) - pre_dE * scalingFactor),
									dx * i + ColorSequenceEditor.lPad, ColorSequenceEditor.tPad
											+ (int) ((height - ColorSequenceEditor.tPad) - dE * scalingFactor));
						}
						pre_dE = dE;

					}
				} else {

					int i, j1;
					int dx1 = (width - ColorSequenceEditor.lPad - ColorSequenceEditor.rPad) / noOfObjects;
					int preX = 0;
					for (i = 1, j1 = ColorSequenceEditor.lPad; i < noOfObjects; i++, j1 += dx) {
						dx = (width - ColorSequenceEditor.lPad - ColorSequenceEditor.rPad) / noOfObjects;

						dx = (int) (dx * ((ColorSequenceEditor.uvPointList.get(i).range
								- ColorSequenceEditor.uvPointList.get(i - 1).range) / (1.0d / (noOfObjects - 1))));

						dE = Math
								.sqrt(Math
										.pow(ColorSequenceEditor.uvPointList.get(i).l
												- ColorSequenceEditor.uvPointList.get(i - 1).l, 2)
										+ Math.pow(
												(ColorSequenceEditor.uvPointList.get(i).u
														- ColorSequenceEditor.uvPointList.get(i - 1).u) * .15,
												2)
										+ Math.pow((ColorSequenceEditor.uvPointList.get(i).v
												- ColorSequenceEditor.uvPointList.get(i - 1).v) * .15, 2));

						g.setColor(vertLinesColor);
						g.drawLine(dx1 * i + ColorSequenceEditor.lPad, ColorSequenceEditor.tPad,
								dx1 * i + ColorSequenceEditor.lPad, height);
						g.setColor(Color.RED);
						g.fillOval(j1 + dx - 4, ColorSequenceEditor.tPad
								+ (int) ((height - ColorSequenceEditor.tPad) - dE * scalingFactor - 4), 8, 8);

						g.setColor(Color.WHITE);
						if (i > 1) {
							g.drawLine(preX,
									ColorSequenceEditor.tPad
											+ (int) ((height - ColorSequenceEditor.tPad) - pre_dE * scalingFactor),
									j1 + dx, ColorSequenceEditor.tPad
											+ (int) ((height - ColorSequenceEditor.tPad) - dE * scalingFactor));
						}
						pre_dE = dE;
						preX = j1 + dx;
					}

				}

			}

			if (ColorSequenceEditor.td < .0001)
				ColorSequenceEditor.td = 0;
			else {
				ColorSequenceEditor.td = (int) ColorSequenceEditor.td
						+ (double) ((int) ((ColorSequenceEditor.td - (int) ColorSequenceEditor.td) * 10) / 10.0d);
				g.setColor(Color.WHITE);
				g.drawString("Total Discrimination: " + ColorSequenceEditor.td, width / 2 - 75, 30);
			}
		}

	}

	public static void loadImg(int w1, int h1) {

		// TODO Auto-generated method stub
		img = new BufferedImage(w1, h1, BufferedImage.TYPE_3BYTE_BGR);
		wRaster = img.getRaster();
		try {
			String s[];
			double rgb[] = new double[3];
			int height = 400;
			int width = 400;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
			int result = fileChooser.showOpenDialog(ColorSequenceEditor._colorPanel);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				FileReader f = new FileReader(selectedFile);
				BufferedReader br = new BufferedReader(f);
				ColorSequenceEditor.cm.setTitle(ColorSequenceEditor.title + selectedFile.getPath().toString());
				String s1 = br.readLine();
				double data[] = new double[height * width];
				double minValue = 1, maxValue = 0;
				int k = 0;
				String split = " ";
				if (s1.contains("\t"))
					split = "\t";

				if (selectedFile.getPath().contains("TXT") || selectedFile.getPath().contains("txt")) {
					for (int j = 0; s1 != null; s1 = br.readLine()) {
						s = s1.split(split);
						for (j = 0; j < s.length; j++, k++) {
							data[k] = Double.parseDouble(s[j]);
							if (minValue > data[k]) {
								minValue = data[k];
							}
							if (maxValue < data[k]) {
								maxValue = data[k];
							}
						}
					}

					br.close();
					f.close();
					System.out.println("Total Values are " + data.length);
					for (int y = 0, x = 0, i = 0; y < height; y++) {
						for (x = 0; x < width; x++, i++) {
							try {
								rgb = getColor(minValue, maxValue, data[i]);
								wRaster.setPixel(x, y, rgb);
							} catch (Exception ex) {
								System.err.println("Insufficient data will be filled by black");
								rgb[0] = 0;
								rgb[1] = 0;
								rgb[2] = 0;
								wRaster.setPixel(x, y, rgb);
							}
						}
					}
				} else {
					System.err.println("Invalid File");
				}

			} else {
				System.out.println("file not selected");
			}
		} catch (Exception ex) {
			System.out.println("Unable to write the file ..." + ex);
		}

	}

	private static double[] getColor(double minValue, double maxValue, double d) {
		// TODO Auto-generated method stub
		int i = 0;
		double rgb[] = new double[3];
		d = (d - minValue) / (maxValue - minValue);
		if (d == 0.0d) {
			rgb[0] = ColorSequenceEditor.uvPointList.get(0).r;
			rgb[1] = ColorSequenceEditor.uvPointList.get(0).g;
			rgb[2] = ColorSequenceEditor.uvPointList.get(0).b;
		} else if (d == 1.0d) {
			rgb[0] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.uvPointList.size() - 1).r;
			rgb[1] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.uvPointList.size() - 1).g;
			rgb[2] = ColorSequenceEditor.uvPointList.get(ColorSequenceEditor.uvPointList.size() - 1).b;
		} else {
			for (i = 0; i < ColorSequenceEditor.uvPointList.size(); i++) {
				if (ColorSequenceEditor.uvPointList.get(i).range >= d) {
					break;
				}
			}
			double r2 = ColorSequenceEditor.uvPointList.get(i).range - d;
			double r1 = d - ColorSequenceEditor.uvPointList.get(i - 1).range;
			rgb[0] = (ColorSequenceEditor.uvPointList.get(i).r * r1 + ColorSequenceEditor.uvPointList.get(i - 1).r * r2)
					/ (r1 + r2);
			rgb[1] = (ColorSequenceEditor.uvPointList.get(i).g * r1 + ColorSequenceEditor.uvPointList.get(i - 1).g * r2)
					/ (r1 + r2);
			rgb[2] = (ColorSequenceEditor.uvPointList.get(i).b * r1 + ColorSequenceEditor.uvPointList.get(i - 1).b * r2)
					/ (r1 + r2);
		}

		rgb = ColorSequenceEditor.convertToIntRGB(rgb);
		return rgb;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(.6f, .6f, .6f));
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		width = getWidth();
		height = getHeight();
		if (ColorSequenceEditor.showGraph) {
			updateGraph(g);
		} else {
			g.drawImage(img, 0, 0, null);
		}

	}
}
