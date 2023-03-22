import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {

    private List<BufferedImage> frames;
    private int frameIndex = 0;
    private long deltaTime;
    private long previousTime;


    public Animation(int deltaTime){
        this.deltaTime = deltaTime;
        frames = new ArrayList<BufferedImage>();
        previousTime = 0;
    }


    public void updateFrame(){
        if (System.currentTimeMillis() - previousTime >= deltaTime) {
            frameIndex ++;
            if(frameIndex >= frames.size()){
                frameIndex = 0 ;
            }
            previousTime = System.currentTimeMillis();
        }
    }


    public void addFrame(BufferedImage frame){
        frames.add(frame);
    }

    public BufferedImage getFrame(){

        if(frames.size() >0 ){
            return frames.get(frameIndex);
        }
        return null;
    }

}
