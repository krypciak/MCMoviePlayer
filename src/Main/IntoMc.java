package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class IntoMc {

	private static void generateDatapack(String[] names, String[] content, String name, String path)
			throws IOException {
		String root = path + "\\" + "MCMoviePlayer" + "\\";
		File rootFile = new File(root);
		rootFile.mkdirs();
		File packmeta = new File(root + "pack.mcmeta");
		packmeta.createNewFile();
		PrintWriter w = new PrintWriter(packmeta);
		String packmetacontent = "{\n" + "	\"pack\": {\r\n" + "		\"pack_format\": 6,\r\n"
				+ "		\"description\": \"" + name + "\"\r\n" + "	}\r\n" + "}";
		w.println(packmetacontent);
		w.close();
		// MCMoviePlayer for MCMulator Project
		String funcFolderPath = root + "data\\mmp\\functions";
		File funcFolder = new File(funcFolderPath);
		funcFolder.mkdirs();

		for (int i = 0; i < names.length; i++) {
			File func = new File(funcFolderPath + "\\" + names[i] + ".mcfunction");
			func.createNewFile();
			PrintWriter wr = new PrintWriter(func);
			wr.println(content[i]);
			wr.close();
		}

	}

	public static void generateCompressedVideo(BufferedImage[] frames, String path, int tickDelay, int resX, int resY,
			int compression) throws IOException {
		int len = frames.length;
		String[] names = new String[len];
		String[] content = new String[len];

		names[0] = "start";
		content[0] = "scoreboard players set mmpstop mcm 0\n" + generateImage(frames[0]) + "\nfunction mmp:1";

		for (int i = 1; i < len; i++) {
			StringBuilder b = new StringBuilder();
			names[i] = String.valueOf(i);
			b.append(compressedImage(frames[i - 1], frames[i], compression));

			b.append("execute if score mmpstop mcm matches 0 run schedule function mmp:" + (i + 1) + " " + tickDelay
					+ "t");
			content[i] = b.toString();

			double progress = ((double) i / ((double) len - 1)) * 100;
			System.out.println("Progress " + (int) progress + "%");

		}

		generateDatapack(names, content, "MCMoviePlayer compressed video", path);
	}

	private static String compressedImage(BufferedImage last, BufferedImage current, int compression) {
		StringBuilder b = new StringBuilder();
		int width = current.getWidth();
		int height = current.getHeight();

		for (int y = 0; y < height / 3; y++) {
			for (int x = 0; x < width / 3; x++) {

				for (int h = 0; h < 3; h++) {
					int mx = x;
					int my = 22 - h;
					int mz = y;

					String type = "";

					int imgPosX[] = new int[] { x * 3 + 2, x * 3 + 1, x * 3 };
					int imgPosY = y * 3 + (2 - h);

					Color oc[] = new Color[] { new Color(last.getRGB(imgPosX[0], imgPosY)),
							new Color(last.getRGB(imgPosX[1], imgPosY)), new Color(last.getRGB(imgPosX[2], imgPosY)) };

					Color c[] = new Color[] { new Color(current.getRGB(imgPosX[0], imgPosY)),
							new Color(current.getRGB(imgPosX[1], imgPosY)),
							new Color(current.getRGB(imgPosX[2], imgPosY)) };

					for (int j = 0; j < 3; j++) {
						if (c[j].getRed() - compression <= oc[j].getRed()
								&& c[j].getRed() + compression >= oc[j].getRed()
								&& c[j].getGreen() - compression <= oc[j].getGreen()
								&& c[j].getGreen() + compression >= oc[j].getGreen()
								&& c[j].getBlue() - compression <= oc[j].getBlue()
								&& c[j].getBlue() + compression >= oc[j].getBlue()) {
							type += "0";
						} else
							type += "1";

					}
					if (type.charAt(1) == '1' || type.charAt(2) == '1') {
						type = type.charAt(0) + "1";
					}

					b.append(cmd(type, mx, my, mz, calcRGB(c[0]), calcRGB(c[1]), calcRGB(c[2]), h));

				}
			}

		}

		return b.toString();
	}

	public static void generateUncompressedVideo(BufferedImage[] frames, String path, int tickDelay, int resX, int resY)
			throws IOException {
		int len = frames.length;
		String[] names = new String[len];
		String[] content = new String[len];

		names[0] = "start";
		content[0] = "scoreboard players set mmpstop mcm 0\n" + generateImage(frames[0]) + "\nfunction mmp:1";

		for (int i = 1; i < len; i++) {
			StringBuilder b = new StringBuilder();
			names[i] = String.valueOf(i);
			b.append(generateImage(frames[i]));

			// b.append("say Frame " + i + " done.\n");
			b.append("execute if score mmpstop mcm matches 0 run schedule function mmp:" + (i + 1) + " " + tickDelay
					+ "t");
			content[i] = b.toString();

			double progress = ((double) i / ((double) len - 1)) * 100;
			System.out.println("Progress " + (int) progress + "%");

		}

		generateDatapack(names, content, "MCMoviePlayer uncompressed video", path);
	}

	public static void generateImageDatapack(BufferedImage image, String path) throws IOException {
		String cmd = generateImage(image);
		generateDatapack(new String[] { "draw" }, new String[] { cmd }, "MCMoviePlayer image", path);
	}

	private static String secmd(int mx, int my, int mz, int c1, int c2, int c3, int h) {
		return "setblock " + mx + " " + my + " " + mz
				+ " spawner{MaxNearbyEntities:0,SpawnData:{id:\"minecraft:armor_stand\",Marker:1b,Invisible:1b,Pose:{LeftArm:[0f,0f,0f],RightArm:[0f,0f,0f]},HandItems:[{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:"
				+ c2 + "},CustomModelData:" + (h * 3 + 2)
				+ "}},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:" + c3 + "},CustomModelData:"
				+ (h * 3 + 3)
				+ "}}],ArmorItems:[{},{},{},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:" + c1
				+ "},CustomModelData:" + (h * 3 + 1) + "}}]}} destroy\n";
	}
	
	private static String cmd(String type, int mx, int my, int mz, int c1, int c2, int c3, int h) {

		switch (type) {
		case "11":
			return "data merge block " + mx + " " + my + " " + mz
					+ " {SpawnData:{id:\"minecraft:armor_stand\",HandItems:[{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:"
					+ c2 + "},CustomModelData:" + (h * 3 + 2)
					+ "}},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:" + c3 + "},CustomModelData:"
					+ (h * 3 + 3)
					+ "}}],ArmorItems:[{},{},{},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:" + c1
					+ "},CustomModelData:" + (h * 3 + 1) + "}}]}}\n";
		case "00":
			return "";

		case "10":
			return "data merge block " + mx + " " + my + " " + mz
					+ " {SpawnData:{id:\"minecraft:armor_stand\",ArmorItems:[{},{},{},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:"
					+ c1 + "},CustomModelData:" + (h * 3 + 1) + "}}]}} destroy\n";
		case "01":
			return "data merge block " + mx + " " + my + " " + mz
					+ " {SpawnData:{id:\"minecraft:armor_stand\",HandItems:[{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:"
					+ c2 + "},CustomModelData:" + (h * 3 + 2)
					+ "}},{id:\"minecraft:leather_boots\",Count:1b,tag:{display:{color:" + c3 + "},CustomModelData:"
					+ (h * 3 + 3)
					+ "}}]}}\n";

		default:
			return "";

		}

	}

	private static String generateImage(BufferedImage image) throws IOException {
		if (image == null) {
			return null;
		}
		int width = image.getWidth();
		int height = image.getHeight();

		StringBuilder command = new StringBuilder();
		for (int y = 0; y < height / 3; y++) {
			for (int x = 0; x < width / 3; x++) {

				for (int h = 0; h < 3; h++) {
					int mx = x;
					int my = 22 - h;
					int mz = y;

					int c1 = calcRGB(new Color(image.getRGB(x * 3 + 2, y * 3 + (2 - h))));
					int c2 = calcRGB(new Color(image.getRGB(x * 3 + 1, y * 3 + (2 - h))));
					int c3 = calcRGB(new Color(image.getRGB(x * 3, y * 3 + (2 - h))));

					command.append(secmd(mx, my, mz, c1, c2, c3, h));

				}
			}

		}

		return command.toString();
	}

	private static int calcRGB(Color c) {
		return c.getBlue() + (c.getGreen() * 256) + (c.getRed() * 65536);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
}
