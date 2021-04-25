package Main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Main {

	public static void main(String[] args) throws IOException {

		if (args.length != 6) {
			System.err.println(
					"Program needs to have 6 arguments.\n *Video/Image Path* *Output datapack path* *Resolution X* *Resolution Y* *Frames per second* *Compresion*");
			return;
		}
		int resX;
		int resY;

		try {
			resX = Integer.parseInt(args[2]);
		} catch (java.lang.NumberFormatException e) {
			System.err.println("Minecraft screen size X must be an integer.");
			return;
		}
		try {
			resY = Integer.parseInt(args[3]);
		} catch (java.lang.NumberFormatException e) {
			System.err.println("Minecraft screen size Y must be an integer.");
			return;
		}

		int framerate;
		try {
			framerate = Integer.parseInt(args[4]);
		} catch (java.lang.NumberFormatException e) {
			System.err.println("Framerate must be an integer.");
			return;
		}
		if (framerate < 1 || framerate > 20) {
			System.err.println("Framerate must be betwenn numbers 1 and 20");
			return;
		}

		int compr;
		try {
			compr = Integer.parseInt(args[5]);
		} catch (java.lang.NumberFormatException e) {
			System.err.println("Compresion must be an integer.");
			return;
		}
		if (compr < 0 || compr > 50) {
			System.err.println("Compresion must be betwenn numbers 0 and 50");
			return;
		}

		File videoFile = new File(args[0]);
		if (videoFile.exists() == false) {
			System.err.println("Video file doesn't exist.");
			return;
		}
		String ext = FilenameUtils.getExtension(args[0]);

		BufferedImage[] frames;
		System.out.println("MCMoviePlayer from Project MCMulator");

		File datapackFolder;
		if (args[1].endsWith("\\"))
			datapackFolder = new File(args[1] + "MCMoviePlayer");
		else
			datapackFolder = new File(args[1] + "\\MCMoviePlayer");
		if (datapackFolder.exists())
			FileUtils.deleteDirectory(datapackFolder);

		if (ext.equals("mp4")) {

			System.out.println("Processing the video...");
			frames = new VideoProcessing().process(videoFile.getAbsolutePath(), resX, resY, framerate);
			System.out.println("Done.\nBuilding datapack...");
			//IntoMc.generateUncompressedVideo(frames, args[1], (20 / framerate), resX, resY);
			IntoMc.generateCompressedVideo(frames, args[1], (20 / framerate), resX, resY, compr);
			
			

		} else if (ext.equals("png") || ext.equals("jpg")) {
			System.out.println("Processing the image...");
			BufferedImage image = IntoMc.toBufferedImage(ImageIO.read(videoFile).getScaledInstance(resX, resY, Image.SCALE_SMOOTH));
			
			System.out.println("Done.");
			System.out.println("Building datapack...");

			IntoMc.generateImageDatapack(image, args[1]);
			
		} else {
			System.out.println("Invalid file format.");
			return;
		}

		System.out.println("Done.");

		System.out.println("All tasks finished.");

	}

}
