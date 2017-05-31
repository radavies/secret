package secret;

import java.util.ArrayList;

public class ImageInformation {

    private int startAt;
    private int endAt;
    private ArrayList<Integer> ignoreLocations;

    public ImageInformation(){
        startAt = 0;
        endAt = 0;
        ignoreLocations = new ArrayList<>();
    }

    public void addIgnoreLocation(int location){
        ignoreLocations.add(location);
    }

    public void setStartLocation(int location){
        startAt = location;
    }

    public int getStartLocation(){
        return startAt;
    }

    public void setEndLocation(int location){
        endAt = location;
    }

    public int getEndLocation(){
        return endAt;
    }

    public int availableBytes(){
        int availableBytes = endAt - startAt;
        availableBytes -= ignoreLocations.size();
        return availableBytes;
    }

    public boolean isByteAvailable(int byteLocation){
        if(byteLocation < startAt
                || byteLocation >= endAt) {
            return false;
        }

        return !ignoreLocations.contains(byteLocation);

    }
}
