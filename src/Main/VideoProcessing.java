package Main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
public class VideoProcessing {
		
	
	public BufferedImage[] process(String input, int resX, int resY, int framerate) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception{
		avutil.av_log_set_level(avutil.AV_LOG_QUIET);
		Java2DFrameConverter bimConverter = new Java2DFrameConverter();
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
	    grabber.start();
	    
	    List<BufferedImage> list = new ArrayList<>();
	    int oldFps = (int) grabber.getVideoFrameRate();
	    System.out.println("Framerate "+oldFps+" -> "+framerate);
	    int count = 0;
	    double framecount = grabber.getLengthInFrames()/(oldFps/framerate);
	    Frame frame;
	    System.out.println("Framecount: "+framecount);
	    while ((frame = grabber.grabImage()) != null) {
	    	if(count++ % (oldFps/framerate) == 0) {
	    		
	    		list.add(toBufferedImage(bimConverter.convert(frame).getScaledInstance(resX, resY, Image.SCALE_DEFAULT)));
	    		double currentFrame = ((count-1)/(oldFps/framerate));
	    		double progress =(currentFrame/framecount)*100;
	    		System.out.println("Progress "+(int)progress+"%");
	    	}
	    }
	    
	    BufferedImage[] frames = new BufferedImage[list.size()];
	    for(int i=0; i<list.size(); i++) {
	    	frames[i] = list.get(i);
	    }
	    
		grabber.close();
		return frames;
	} 
	
	
	private BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
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
