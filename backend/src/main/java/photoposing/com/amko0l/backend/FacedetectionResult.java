package photoposing.com.amko0l.backend;

/**
 * Created by amko0l on 4/25/2017.
 */

public class FacedetectionResult {
    String panAngle;
    String tiltAngle;
    String landmarkingConfidence;
    String joyLikelihood;
    String sorrowLikelihood;
    String angerLikelihood;

    @Override
    public String toString() {
        return String.valueOf(panAngle)+ String.valueOf(tiltAngle) +
                String.valueOf(landmarkingConfidence) + String.valueOf(joyLikelihood)
                +sorrowLikelihood+angerLikelihood;
    }
}
