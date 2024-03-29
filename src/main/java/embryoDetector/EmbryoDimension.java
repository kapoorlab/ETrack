package embryoDetector;

public enum EmbryoDimension {
	NONE, CURVATURE, DISTCURVATURE, LineIntensity, INTENSITY, INTENSITY_SQUARED, POSITION, VELOCITY, LENGTH, // we separate length and
																								// position so that
																								// x,y,z are plotted on
																								// a different graph
																								// from spot sizes
	TIME, RATE, // count per frames
	STRING; // for non-numeric features
}
